import { useEffect, useState } from 'react';
import { Image as KonvaImage, Group, Rect, Text, Circle, Label, Tag } from 'react-konva';
import { Html } from 'react-konva-utils';
import { Trash2 } from 'lucide-react';
import useImage from 'use-image';
import { useAIAnalysis } from '../../hooks/useAIAnalysis';
import { useCanvasStore } from '../../store/useCanvasStore';
import { useAppStore } from '../../store/useAppStore';
import type { ScreenNode } from '../../types';

interface ScreenNodeProps {
  screen: ScreenNode;
}

export function ScreenNodeComponent({ screen }: ScreenNodeProps) {
  const [image] = useImage(screen.imageUrl);
  const [base64, setBase64] = useState<string>('');
  const [hoveredAnnotationIdx, setHoveredAnnotationIdx] = useState<number | null>(null);
  const { analyze, isAnalyzing, error } = useAIAnalysis();
  const removeScreen = useCanvasStore((state) => state.removeScreen);
  const updateScreenPosition = useCanvasStore((state) => state.updateScreenPosition);
  const activeTool = useAppStore((state) => state.activeTool);

  useEffect(() => {
    if (image) {
      const canvas = document.createElement('canvas');
      canvas.width = image.width;
      canvas.height = image.height;
      const ctx = canvas.getContext('2d');
      if (ctx) {
        ctx.drawImage(image, 0, 0);
        setBase64(canvas.toDataURL('image/png').split(',')[1] || '');
      }
    }
  }, [image]);

  const annotations = screen.annotations || [];

  return (
    <Group
      x={screen.position.x}
      y={screen.position.y}
      draggable={activeTool === 'select'}
      onDragEnd={(e) => {
        updateScreenPosition(screen.id, { x: e.target.x(), y: e.target.y() });
      }}
    >

      {/* Sleek drop shadow for the image */}
      <Rect
        x={0} y={0}
        width={screen.size.width} height={screen.size.height}
        fill="#000"
        shadowColor="#000" shadowBlur={40} shadowOpacity={0.25} shadowOffsetY={10}
        cornerRadius={4}
      />

      {/* Actual Image */}
      {image && (
        <Group x={0} y={0} clipX={0} clipY={0} clipWidth={screen.size.width} clipHeight={screen.size.height}>
          <KonvaImage
            image={image}
            width={screen.size.width}
            height={screen.size.height}
          />
        </Group>
      )}

      {/* Render Annotations via AI */}
      {annotations.map((ann, idx) => {
        const ax = ann.x > 1 ? ann.x : ann.x * screen.size.width;
        const ay = ann.y > 1 ? ann.y : ann.y * screen.size.height;
        const isHovered = hoveredAnnotationIdx === idx;
        const isAnyHovered = hoveredAnnotationIdx !== null;

        return (
          <Group
            key={idx} x={ax} y={ay}
            opacity={isHovered ? 1 : (isAnyHovered ? 0 : 0.6)}
            onMouseEnter={(e) => {
              const container = e.target.getStage()?.container();
              if (container) container.style.cursor = 'pointer';
              setHoveredAnnotationIdx(idx);
            }}
            onMouseLeave={(e) => {
              const container = e.target.getStage()?.container();
              if (container) container.style.cursor = 'default';
              setHoveredAnnotationIdx(null);
            }}
          >
            {/* Pulsing ring */}
            <Circle radius={16} fill="rgba(255, 255, 255, 0.2)" stroke="#ffffff" strokeWidth={1.5} />
            {/* Core dot */}
            <Circle radius={4} fill="#ffffff" />

            {/* Text Balloon */}
            <Label x={24} y={-16}>
              <Tag
                fill={isHovered ? "#000000" : "rgba(19, 19, 20, 0.85)"}
                cornerRadius={8}
                shadowColor="#000" shadowBlur={15} shadowOpacity={0.3} shadowOffsetY={5}
                stroke={isHovered ? "#ffffff" : "rgba(255, 255, 255, 0.4)"} strokeWidth={1}
              />
              <Text
                text={ann.issue}
                fill="#e5e5e5" fontSize={12} width={196}
                fontFamily="Inter" lineHeight={1.4}
                padding={12}
              />
            </Label>
          </Group>
        );
      })}

      {/* Floating Action Pill at the bottom center of the image */}
      <Group x={screen.size.width / 2 - 132} y={screen.size.height + 16}>
        <Rect width={264} height={44} fill="#1a1a1f" cornerRadius={22} shadowColor="#000" shadowBlur={15} shadowOpacity={0.4} stroke="rgba(255,255,255,0.05)" strokeWidth={1} />

        {/* Analyze UX Button */}
        <Group
          x={6} y={6}
          onPointerDown={() => { if (!isAnalyzing && base64) analyze(screen, base64); }}
          onMouseEnter={(e) => {
            const container = e.target.getStage()?.container();
            if (container) container.style.cursor = 'pointer';
          }}
          onMouseLeave={(e) => {
            const container = e.target.getStage()?.container();
            if (container) container.style.cursor = 'default';
          }}
        >
          <Rect width={208} height={32} fill={isAnalyzing ? "#262627" : "#ffffff"} cornerRadius={16} />

          <Text
            text={isAnalyzing ? "Analyzing UX..." : "Analyze Screen"}
            fill={isAnalyzing ? "#8a8a8e" : "#000000"}
            x={0} y={0}
            width={208} height={32}
            align="center" verticalAlign="middle"
            fontFamily="Inter" fontSize={13} fontStyle="bold"
          />
        </Group>

        {/* Delete Button (Trash Icon) */}
        <Group
          x={222} y={6}
          onPointerDown={() => removeScreen(screen.id)}
          onMouseEnter={(e) => {
            const container = e.target.getStage()?.container();
            if (container) container.style.cursor = 'pointer';
          }}
          onMouseLeave={(e) => {
            const container = e.target.getStage()?.container();
            if (container) container.style.cursor = 'default';
          }}
        >
          <Rect width={36} height={32} fill="#ff4d4d" cornerRadius={16} />

          <Group x={10} y={8}>
            <Html divProps={{ style: { pointerEvents: 'none' } }}>
              <Trash2 size={16} color="#ffffff" strokeWidth={2} />
            </Html>
          </Group>
        </Group>

      </Group>

      {error && (
        <Text
          x={0} y={screen.size.height + 70}
          text={`Warning: ${error}`}
          fill="#ff6b6b"
          fontFamily="Inter" fontSize={11} width={screen.size.width} align="center"
        />
      )}
    </Group>
  );
}
