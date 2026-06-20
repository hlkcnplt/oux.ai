import { create } from 'zustand';
import type { ProviderStore } from '../types';
import { AIProvider } from '../types';

export const useProviderStore = create<ProviderStore>((set) => ({
  provider: AIProvider.LOCAL,
  apiKey: '',
  localEndpoint: 'http://localhost:11434/v1',
  modelName: '',
  setProvider: (provider) => set({ provider }),
  setApiKey: (apiKey) => set({ apiKey }),
  setLocalEndpoint: (localEndpoint) => set({ localEndpoint }),
  setModelName: (modelName) => set({ modelName }),
}));
