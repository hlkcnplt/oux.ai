
export const AIProvider = {
  GEMINI: 'GEMINI',
  LOCAL: 'LOCAL',
} as const;

export type AIProvider = typeof AIProvider[keyof typeof AIProvider];

export interface ProviderState {
  provider: AIProvider;
  apiKey: string;
  localEndpoint: string;
  modelName: string;
}

export interface ProviderActions {
  setProvider: (provider: AIProvider) => void;
  setApiKey: (key: string) => void;
  setLocalEndpoint: (url: string) => void;
  setModelName: (name: string) => void;
}

export type ProviderStore = ProviderState & ProviderActions;


export interface Position {
  x: number;
  y: number;
}

export interface Size {
  width: number;
  height: number;
}

export interface AnnotationResult {
  x: number;
  y: number;
  issue: string;
  severity: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
}

export interface ScreenNode {
  id: string;
  dbId?: string;
  position: Position;
  size: Size;
  imageUrl: string;
  name: string;
  annotations?: AnnotationResult[];
}

export interface Project {
  id: string;
  dbId?: string;
  name: string;
  screens: ScreenNode[];
}


export type ActiveTool = 'select' | 'hand' | 'comment';

export interface AppState {
  isSidebarOpen: boolean;
  isSettingsOpen: boolean;
  activeTool: ActiveTool;
}

export interface AppActions {
  setSidebarOpen: (isOpen: boolean) => void;
  setSettingsOpen: (isOpen: boolean) => void;
  setActiveTool: (tool: ActiveTool) => void;
}

export type AppStore = AppState & AppActions;


export interface AnalysisRequest {
  screenId: string;
  provider: AIProvider;
  apiKey: string;
  imageBase64: string;
  localEndpoint?: string;
  modelName?: string;
}

export interface AnalysisResult {
  reportId: string;
  annotations: AnnotationResult[];
}

export interface CreateProjectPayload {
  name: string;
  description: string;
}

export interface CreateScreenPayload {
  projectId: string;
  versionTag: string;
  imageUrl: string;
  canvasX: number;
  canvasY: number;
  canvasScale: number;
}

export interface BackendProject {
  id: string;
  name: string;
  description: string;
  createdAt: string;
}

export interface BackendScreen {
  id: string;
  projectId: string;
  versionTag: string;
  imageUrl: string;
  canvasX: number;
  canvasY: number;
  canvasScale: number;
  createdAt: string;
  annotations: AnnotationResult[];
}
