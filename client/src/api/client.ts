import type { AnalysisRequest, AnalysisResult, CreateProjectPayload, CreateScreenPayload, BackendProject, BackendScreen } from '../types';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  });
  if (!response.ok) {
    const errData = await response.json().catch(() => null);
    throw new Error(errData?.error || `API Error: ${response.statusText}`);
  }
  const json = await response.json();
  if (json.error) throw new Error(json.error);
  return json.data as T;
}

export const apiClient = {
  createProject: (payload: CreateProjectPayload): Promise<BackendProject> =>
    request('/api/v1/projects', { method: 'POST', body: JSON.stringify(payload) }),

  createScreen: (payload: CreateScreenPayload): Promise<BackendScreen> =>
    request('/api/v1/screens', { method: 'POST', body: JSON.stringify(payload) }),

  analyzeScreen: (payload: AnalysisRequest): Promise<AnalysisResult> =>
    request('/api/v1/analysis', { method: 'POST', body: JSON.stringify(payload) }),
};

