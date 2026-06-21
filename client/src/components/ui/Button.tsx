import type { ButtonHTMLAttributes } from 'react';

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'tertiary';
  fullWidth?: boolean;
}

export function Button({ variant = 'primary', fullWidth, className = '', children, ...props }: ButtonProps) {
  const baseClasses = "px-4 py-2.5 text-xs font-bold uppercase tracking-wider rounded-xl transition-all duration-200 inline-flex items-center justify-center gap-2 cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed";
  
  const variants = {
    primary: "btn-gradient text-black shadow-lg hover:brightness-110",
    secondary: "bg-[var(--color-surface-container-highest)] text-[var(--color-on-surface)] hover:bg-[var(--color-outline-variant)]",
    tertiary: "bg-transparent text-[var(--color-primary)] hover:bg-[var(--color-surface-container)]"
  };

  const widthClass = fullWidth ? "w-full" : "";

  return (
    <button className={`${baseClasses} ${variants[variant]} ${widthClass} ${className}`} {...props}>
      {children}
    </button>
  );
}
