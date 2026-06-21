import { Settings, Share2, PanelLeft } from 'lucide-react';
import { useAppStore } from '../../store/useAppStore';
import { useCanvasStore } from '../../store/useCanvasStore';

export function Header() {
  const setSettingsOpen = useAppStore((state) => state.setSettingsOpen);
  const isSidebarOpen = useAppStore((state) => state.isSidebarOpen);
  const setSidebarOpen = useAppStore((state) => state.setSidebarOpen);
  const projects = useCanvasStore((state) => state.projects);
  const activeProjectId = useCanvasStore((state) => state.activeProjectId);

  return (
    <div className="absolute top-0 w-full p-4 flex justify-between items-center z-40 pointer-events-none">
      {/* Left side */}
      <div className="flex items-center gap-3 pointer-events-auto">
        <button 
          onClick={() => setSidebarOpen(!isSidebarOpen)}
          className={`w-9 h-9 flex items-center justify-center rounded-xl transition-all border border-[var(--color-outline-variant)]/20 shadow-sm ${isSidebarOpen ? 'bg-[#333336] text-white' : 'bg-[#1e1e20] text-[var(--color-on-surface-variant)] hover:text-white hover:bg-[#262627]'}`}
          title="Toggle Projects"
        >
          <PanelLeft size={18} />
        </button>  
        <img 
          src="/favicon.svg" 
          alt="oux" 
          className="w-9 h-9 rounded-xl shadow-lg cursor-pointer hover:brightness-110 transition-all bg-white p-1"
        />
        <div className="glass-panel px-4 py-2 rounded-xl text-sm font-medium border border-[var(--color-outline-variant)]/20 shadow-sm cursor-pointer hover:bg-[var(--color-surface-container-high)]/60 transition-colors">
          {projects.find(p => p.id === activeProjectId)?.name || 'Untitled Design'}
        </div>
      </div>

      {/* Right side */}
      <div className="flex items-center gap-2 pointer-events-auto">
        <button 
            onClick={() => {
                const stage = useCanvasStore.getState().stageRef;
                if (stage) {
                    const dataURL = (stage as any).toDataURL({ pixelRatio: 2 });
                    const link = document.createElement('a');
                    link.download = 'oux-ai-export.png';
                    link.href = dataURL;
                    document.body.appendChild(link);
                    link.click();
                    document.body.removeChild(link);
                }
            }}
            className="h-9 px-4 flex items-center gap-2 bg-[#1e1e20] hover:bg-[#262627] text-white rounded-xl text-sm font-medium transition-colors border border-[var(--color-outline-variant)]/20 shadow-sm disabled:opacity-50"
        >
          <Share2 size={16} />
          Share
        </button>
        <button
          onClick={() => setSettingsOpen(true)}
          className="w-9 h-9 flex items-center justify-center rounded-xl bg-[#1e1e20] hover:bg-[#262627] text-[var(--color-on-surface-variant)] hover:text-white transition-all border border-[var(--color-outline-variant)]/20 shadow-sm"
          title="Settings"
        >
          <Settings size={18} />
        </button>
      </div>
    </div>
  );
}
