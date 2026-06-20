import { useState, useCallback } from 'react';
import { apiClient } from '../api/client';
import { useProviderStore } from '../store/useProviderStore';
import { useCanvasStore } from '../store/useCanvasStore';
import type { ScreenNode } from '../types';

export function useAIAnalysis() {
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const provider = useProviderStore((state) => state.provider);
  const apiKey = useProviderStore((state) => state.apiKey);
  const localEndpoint = useProviderStore((state) => state.localEndpoint);
  const modelName = useProviderStore((state) => state.modelName);

  const analyze = useCallback(async (screen: ScreenNode, imageBase64: string) => {
    setIsAnalyzing(true);
    setError(null);
    try {
      if (provider !== 'LOCAL' && !apiKey) {
        throw new Error('API Key is missing for cloud provider.');
      }

      let dbScreenId = screen.dbId;

      if (!dbScreenId) {
        const state = useCanvasStore.getState();
        const activeProject = state.projects.find(p => p.id === state.activeProjectId);
        if (!activeProject) throw new Error('No active project found.');

        let dbProjectId = activeProject.dbId;
        if (!dbProjectId) {
          const created = await apiClient.createProject({ name: activeProject.name, description: '' });
          dbProjectId = created.id;
          useCanvasStore.setState(s => ({
            projects: s.projects.map(p =>
              p.id === s.activeProjectId ? { ...p, dbId: created.id } : p
            )
          }));
        }

        const imageUrl = `data:image/png;base64,${imageBase64}`;
        const createdScreen = await apiClient.createScreen({
          projectId: dbProjectId,
          versionTag: screen.name,
          imageUrl,
          canvasX: screen.position.x,
          canvasY: screen.position.y,
          canvasScale: 1.0,
        });
        dbScreenId = createdScreen.id;

        useCanvasStore.setState(s => ({
          projects: s.projects.map(p =>
            p.id === s.activeProjectId
              ? { ...p, screens: p.screens.map(sc => sc.id === screen.id ? { ...sc, dbId: createdScreen.id } : sc) }
              : p
          )
        }));
      }

      const payload = {
        screenId: dbScreenId,
        provider,
        apiKey,
        localEndpoint,
        modelName,
        imageBase64,
      };

      const result = await apiClient.analyzeScreen(payload);

      useCanvasStore.setState((state) => ({
        projects: state.projects.map(p =>
          p.id === state.activeProjectId
            ? { ...p, screens: p.screens.map(s => s.id === screen.id ? { ...s, annotations: result.annotations } : s) }
            : p
        )
      }));

    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'An unknown error occurred during analysis.';
      setError(message);
    } finally {
      setIsAnalyzing(false);
    }
  }, [provider, apiKey, localEndpoint, modelName]);

  return { analyze, isAnalyzing, error };
}
