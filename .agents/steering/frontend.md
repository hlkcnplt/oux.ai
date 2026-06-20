# OpenDesign — Frontend

## Stack

| Concern        | Library              | Notes                                   |
|----------------|----------------------|-----------------------------------------|
| Framework      | React 19             | Strict Mode enabled by default          |
| Build Tool     | Vite 8               | TypeScript-first                        |
| Styling        | Tailwind CSS v4      | `@import "tailwindcss"` in `index.css`  |
| Canvas         | React-Konva 19       | Konva.js for infinite canvas            |
| State          | Zustand 5            | One store per domain slice              |
| HTTP Client    | fetch / axios        | Abstracted behind custom hooks          |
| Icons          | Lucide React         |                                         |

## Project Structure

```
client/src/
├── assets/               # Static images, fonts
├── components/           # Reusable, headless UI components
│   ├── canvas/           # Konva Stage, Layer, nodes
│   ├── panels/           # Sidebar, settings drawers
│   └── ui/               # Buttons, Modals, Badges
├── hooks/                # All data-fetching and canvas logic
│   ├── useCanvas.ts      # Drag, zoom, pan interactions
│   ├── useAIAnalysis.ts  # Triggers analysis, manages loading state
│   └── useProvider.ts    # Reads AI provider from the store
├── pages/                # Top-level route views
├── store/                # Zustand slices
│   ├── useAppStore.ts    # Global UI state
│   ├── useCanvasStore.ts # Canvas viewport and screen positions
│   └── useProviderStore.ts # AI provider selection + in-memory API keys
├── types/                # Shared TypeScript interfaces
├── api/                  # Typed API client wrappers
│   └── client.ts
├── main.tsx
└── index.css
```

## Zustand Store Rules

- One file per domain slice. Never put unrelated state in the same store.
- Stores must be typed using TypeScript interfaces exported from `types/`.
- The `useProviderStore` slice manages AI provider state. Its shape is:

```typescript
interface ProviderState {
  provider: 'GEMINI' | 'LOCAL';
  apiKey: string;
  localEndpoint: string;
  modelName: string;
  setProvider: (provider: ProviderState['provider']) => void;
  setApiKey: (key: string) => void;
  setLocalEndpoint: (url: string) => void;
  setModelName: (name: string) => void;
}
```

- The API key is held exclusively in Zustand memory. It must never be:
  - Stored in `localStorage` or `sessionStorage`.
  - Appended to any URL as a query parameter.
  - Logged to the browser console.

## Custom Hook Rules

- ALL data-fetching, all canvas interactions, and all AI-trigger logic must live inside custom hooks under `hooks/`.
- Components must not call `fetch` or import the API client directly.
- Hooks must be named with the `use` prefix and placed in `hooks/`.
- A hook responsible for a single domain concern must not grow beyond 150 lines. Split it.

## Component Rules

- Components must be pure presentational or light containers. No business logic.
- All event handlers delegate to hooks.
- Use `React.memo` for canvas node components that re-render on position changes.

## AI Provider UI Requirements

- The header or a persistent sidebar panel must always display the currently active AI provider (e.g. a badge showing `GEMINI` or `LOCAL`).
- A settings panel (`ProviderSettings`) allows the user to:
  1. Select a provider from a dropdown.
  2. Input their API Key (masked input field, `type="password"`).
  3. For `LOCAL`, input the LM Studio / Ollama endpoint URL and model name.
- On first project load, if no provider is configured, the settings panel must open automatically.

## Tailwind CSS Rules

- Use Tailwind CSS v4's `@import "tailwindcss"` syntax in `index.css`. Never use v3 directives (`@tailwind base`, etc.).
- Use `@layer components` for reusable classes that span multiple components.
- Arbitrary values (`text-[14px]`) are allowed only when a design token is not available; prefer configuration.
- Color palette is defined as CSS custom properties in `index.css` and referenced via Tailwind's theme configuration.

## TypeScript Rules

- `strict: true` must remain enabled in `tsconfig.app.json`.
- No `any` types. Use `unknown` and narrow, or define precise interfaces.
- All API response shapes must be defined in `types/`.
- Enums are forbidden; use `const` objects with `as const` and derive the union type.

## API Communication

- All calls to `server-core` go through the typed wrappers in `api/client.ts`.
- The base URL is read from `import.meta.env.VITE_API_URL`.
- When triggering an AI analysis, the payload must include:

```typescript
interface AnalysisRequest {
  screenId: string;
  provider: 'GEMINI' | 'LOCAL';
  apiKey: string;
  localEndpoint?: string;
  modelName?: string;
}
```
