import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { ScreenNode, Position, Project } from '../types';

interface CanvasState {
  stagePosition: Position;
  stageScale: number;
  projects: Project[];
  activeProjectId: string;
  stageRef: unknown | null;
}

interface CanvasActions {
  setStagePosition: (pos: Position) => void;
  setStageScale: (scale: number) => void;
  addScreen: (screen: ScreenNode) => void;
  updateScreenPosition: (id: string, pos: Position) => void;
  removeScreen: (id: string) => void;
  setStageRef: (ref: unknown) => void;
  

  addProject: (name: string) => void;
  deleteProject: (id: string) => void;
  renameProject: (id: string, name: string) => void;
  setActiveProject: (id: string) => void;
}

export type CanvasStore = CanvasState & CanvasActions;

export const useCanvasStore = create<CanvasStore>()(
  persist(
    (set) => ({
      stagePosition: { x: 0, y: 0 },
      stageScale: 1,
      projects: [{ id: 'default', name: 'Untitled Design', screens: [] }],
      activeProjectId: 'default',
      stageRef: null,
      
      setStagePosition: (pos) => set({ stagePosition: pos }),
      setStageScale: (scale) => set({ stageScale: scale }),
      
      addScreen: (screen) => set((state) => ({
        projects: state.projects.map(p => 
          p.id === state.activeProjectId 
            ? { ...p, screens: [...p.screens, screen] }
            : p
        )
      })),
      
      updateScreenPosition: (id, pos) => set((state) => ({
        projects: state.projects.map(p => 
          p.id === state.activeProjectId 
            ? { ...p, screens: p.screens.map(s => s.id === id ? { ...s, position: pos } : s) }
            : p
        )
      })),
      
      removeScreen: (id) => set((state) => ({
        projects: state.projects.map(p => 
          p.id === state.activeProjectId 
            ? { ...p, screens: p.screens.filter(s => s.id !== id) }
            : p
        )
      })),
      
      setStageRef: (ref) => set({ stageRef: ref }),
      
      addProject: (name) => set((state) => {
        const newProject = { id: Date.now().toString(), name, screens: [] };
        return {
          projects: [...state.projects, newProject],
          activeProjectId: newProject.id
        };
      }),
      
      deleteProject: (id) => set((state) => {
        const remaining = state.projects.filter(p => p.id !== id);
        if (remaining.length === 0) {
          remaining.push({ id: 'default', name: 'Untitled Design', screens: [] });
        }
        return {
          projects: remaining,
          activeProjectId: state.activeProjectId === id ? remaining[0].id : state.activeProjectId
        };
      }),
      
      renameProject: (id, name) => set((state) => ({
        projects: state.projects.map(p => p.id === id ? { ...p, name } : p)
      })),
      
      setActiveProject: (id) => set({ activeProjectId: id, stagePosition: { x: 0, y: 0 }, stageScale: 1 }),
      
    }),
    {
      name: 'oux-ai-canvas-storage',
      partialize: (state) => ({ projects: state.projects, activeProjectId: state.activeProjectId }),
    }
  )
);
