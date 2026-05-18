// shared-components.jsx — reusable mini-program chrome + icons + bits
// Globals exposed via window.* at bottom.

const { useState } = React;

// ============================================================
// Icons (細線, 24×24 viewBox, currentColor stroke)
// ============================================================
const Icon = ({ name, size = 22, color = 'currentColor', strokeWidth = 1.6, fill = 'none', style }) => {
  const props = {
    width: size, height: size, viewBox: '0 0 24 24', fill,
    stroke: color, strokeWidth, strokeLinecap: 'round', strokeLinejoin: 'round',
    style: { display: 'block', ...style },
  };
  switch (name) {
    case 'back':    return <svg {...props}><path d="M15 4 L7 12 L15 20"/></svg>;
    case 'close':   return <svg {...props}><path d="M6 6 L18 18 M18 6 L6 18"/></svg>;
    case 'share':   return <svg {...props}><circle cx="6" cy="12" r="2.2"/><circle cx="18" cy="6" r="2.2"/><circle cx="18" cy="18" r="2.2"/><path d="M8 11 L16 7 M8 13 L16 17"/></svg>;
    case 'cart':    return <svg {...props}><path d="M3 4 H5 L7 16 H19 L21 7 H7"/><circle cx="9" cy="20" r="1.3"/><circle cx="17" cy="20" r="1.3"/></svg>;
    case 'bell':    return <svg {...props}><path d="M5 17 H19 L18 14 V10 a6 6 0 0 0 -12 0 V14 Z"/><path d="M10 20 A2 2 0 0 0 14 20"/></svg>;
    case 'search':  return <svg {...props}><circle cx="11" cy="11" r="6"/><path d="M16 16 L20 20"/></svg>;
    case 'gear':    return <svg {...props}><circle cx="12" cy="12" r="3"/><path d="M12 2 V5 M12 19 V22 M2 12 H5 M19 12 H22 M4.5 4.5 L6.5 6.5 M17.5 17.5 L19.5 19.5 M4.5 19.5 L6.5 17.5 M17.5 6.5 L19.5 4.5"/></svg>;
    case 'home':    return <svg {...props}><path d="M3 11 L12 3 L21 11 V20 a1 1 0 0 1 -1 1 H4 a1 1 0 0 1 -1 -1 Z"/><path d="M9 21 V14 H15 V21"/></svg>;
    case 'play':    return <svg {...props}><circle cx="12" cy="12" r="9"/><path d="M10 8 L16 12 L10 16 Z" fill="currentColor"/></svg>;
    case 'tree':    return <svg {...props}><circle cx="12" cy="5" r="2.2"/><circle cx="6" cy="18" r="2.2"/><circle cx="18" cy="18" r="2.2"/><path d="M12 7 V11 M12 11 H6 V16 M12 11 H18 V16"/></svg>;
    case 'user':    return <svg {...props}><circle cx="12" cy="8" r="4"/><path d="M4 20 a8 8 0 0 1 16 0"/></svg>;
    case 'plus':    return <svg {...props}><path d="M12 5 V19 M5 12 H19"/></svg>;
    case 'minus':   return <svg {...props}><path d="M5 12 H19"/></svg>;
    case 'chat':    return <svg {...props}><path d="M4 5 H20 V17 H13 L9 21 V17 H4 Z"/></svg>;
    case 'check':   return <svg {...props}><path d="M5 12 L10 17 L19 7"/></svg>;
    case 'circle':  return <svg {...props}><circle cx="12" cy="12" r="9"/></svg>;
    case 'chevron': return <svg {...props}><path d="M9 6 L15 12 L9 18"/></svg>;
    case 'chevron-down': return <svg {...props}><path d="M6 9 L12 15 L18 9"/></svg>;
    case 'heart':   return <svg {...props}><path d="M12 20 C 4 14, 4 6, 12 8 C 20 6, 20 14, 12 20 Z"/></svg>;
    case 'star':    return <svg {...props}><path d="M12 3 L14.5 9 L21 9.6 L16 14 L17.5 20.5 L12 17 L6.5 20.5 L8 14 L3 9.6 L9.5 9 Z"/></svg>;
    case 'box':     return <svg {...props}><path d="M3 8 L12 4 L21 8 V18 L12 22 L3 18 Z"/><path d="M3 8 L12 12 L21 8 M12 12 V22"/></svg>;
    case 'truck':   return <svg {...props}><path d="M2 7 H14 V17 H2 Z"/><path d="M14 10 H19 L22 13 V17 H14"/><circle cx="6" cy="18" r="1.5"/><circle cx="17" cy="18" r="1.5"/></svg>;
    case 'check-circle': return <svg {...props}><circle cx="12" cy="12" r="9"/><path d="M8 12 L11 15 L16 9"/></svg>;
    case 'headset': return <svg {...props}><path d="M4 14 V12 a8 8 0 0 1 16 0 V14"/><path d="M4 14 V18 a2 2 0 0 0 2 2 H8 V14 Z"/><path d="M20 14 V18 a2 2 0 0 1 -2 2 H16 V14 Z"/></svg>;
    case 'card':    return <svg {...props}><rect x="3" y="6" width="18" height="12" rx="2"/><path d="M3 10 H21"/></svg>;
    case 'coin':    return <svg {...props}><circle cx="12" cy="12" r="9"/><path d="M9 8 H13 a2 2 0 0 1 0 4 H9 V16 M9 12 H14"/></svg>;
    case 'wallet':  return <svg {...props}><path d="M3 7 V18 a1 1 0 0 0 1 1 H20 V8 a1 1 0 0 0 -1 -1 H4 a1 1 0 0 1 0 -2 H18"/><circle cx="17" cy="13" r="1.2" fill="currentColor"/></svg>;
    case 'ticket': return <svg {...props}><path d="M3 7 V11 a2 2 0 0 1 0 2 V17 H21 V13 a2 2 0 0 1 0 -2 V7 Z"/><path d="M12 7 V17" strokeDasharray="2 2"/></svg>;
    case 'people': return <svg {...props}><circle cx="9" cy="8" r="3"/><circle cx="17" cy="9" r="2.5"/><path d="M3 19 a6 6 0 0 1 12 0"/><path d="M14 19 a5 5 0 0 1 7 0"/></svg>;
    case 'gift':   return <svg {...props}><rect x="3" y="9" width="18" height="11" rx="1"/><path d="M3 13 H21"/><path d="M12 9 V20"/><path d="M8 9 a2 2 0 1 1 4 -2 a2 2 0 1 1 4 2"/></svg>;
    case 'medal':  return <svg {...props}><circle cx="12" cy="14" r="6"/><path d="M8 3 L12 9 L16 3"/><path d="M10 14 L12 12 L14 14 L13 17 L12 16 L11 17 Z" fill="currentColor"/></svg>;
    case 'wechat': return <svg width={props.width} height={props.height} viewBox="0 0 24 24" fill="currentColor"><path d="M9 4C5 4 2 6.5 2 10c0 1.8.9 3.4 2.3 4.5L3.5 17l3-1.5c.8.2 1.6.3 2.5.3.3 0 .6 0 .9-.1-.2-.6-.3-1.2-.3-1.8 0-3.3 3.1-6 7-6 .4 0 .8 0 1.2.1C16.8 5.5 13.2 4 9 4zM7 8.5a1 1 0 110 2 1 1 0 010-2zm5 0a1 1 0 110 2 1 1 0 010-2z"/><path d="M22 14.5c0-2.9-2.9-5.3-6.5-5.3S9 11.6 9 14.5c0 3 2.9 5.3 6.5 5.3.7 0 1.4-.1 2-.3l2.3 1.3-.6-2c1.7-1 2.8-2.6 2.8-4.3zM13.5 13.5a.8.8 0 110 1.6.8.8 0 010-1.6zm4 0a.8.8 0 110 1.6.8.8 0 010-1.6z"/></svg>;
    case 'pin':    return <svg {...props}><path d="M12 22 S5 14 5 9 a7 7 0 0 1 14 0 c0 5 -7 13 -7 13Z"/><circle cx="12" cy="9" r="2.5"/></svg>;
    case 'qr':     return <svg {...props}><rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="3" height="3"/><rect x="18" y="18" width="3" height="3"/><rect x="14" y="14" width="7" height="3" fill="none"/></svg>;
    case 'shop':   return <svg {...props}><path d="M4 9 L5 5 H19 L20 9"/><path d="M4 9 V20 H20 V9"/><path d="M10 20 V13 H14 V20"/></svg>;
    case 'filter': return <svg {...props}><path d="M3 5 H21 M6 12 H18 M10 19 H14"/></svg>;
    case 'sort':   return <svg {...props}><path d="M7 5 V19 M7 19 L4 16 M7 19 L10 16 M17 19 V5 M17 5 L14 8 M17 5 L20 8"/></svg>;
    case 'eye':    return <svg {...props}><path d="M2 12 S5 5 12 5 S22 12 22 12 S19 19 12 19 S2 12 2 12 Z"/><circle cx="12" cy="12" r="3"/></svg>;
    case 'edit':   return <svg {...props}><path d="M4 20 L4 16 L16 4 L20 8 L8 20 Z"/><path d="M13 7 L17 11"/></svg>;
    case 'trash':  return <svg {...props}><path d="M4 7 H20 M9 7 V4 H15 V7 M6 7 L7 20 H17 L18 7"/></svg>;
    case 'info':   return <svg {...props}><circle cx="12" cy="12" r="9"/><path d="M12 8 V8.1 M12 11 V17"/></svg>;
    case 'warn':   return <svg {...props}><path d="M12 3 L22 20 H2 Z"/><path d="M12 10 V14 M12 17 V17.1"/></svg>;
    case 'reload': return <svg {...props}><path d="M21 12 a9 9 0 1 1 -3 -6.7"/><path d="M21 4 V10 H15"/></svg>;
    case 'wifi-off': return <svg {...props}><path d="M2 6 L22 6 M4 18 L20 18" strokeDasharray="2 3"/><circle cx="12" cy="15" r="1.5" fill="currentColor"/></svg>;
    default: return null;
  }
};

// ============================================================
// Status Bar — iOS 9:41 + signal/wifi/battery
// ============================================================
const StatusBar = ({ theme = 'dark', dynamicIsland = false }) => {
  const color = theme === 'dark' ? '#fff' : '#1A1006';
  return (
    <div className={`cp-status ${theme}`}>
      <div>9:41</div>
      {dynamicIsland && (
        <div style={{
          position: 'absolute', left: '50%', top: 11, transform: 'translateX(-50%)',
          width: 124, height: 36, borderRadius: 22, background: '#000', zIndex: 1,
        }}/>
      )}
      <div className="icons" style={{ color }}>
        {/* signal */}
        <svg width="18" height="12" viewBox="0 0 18 12" fill={color}>
          <rect x="0" y="8" width="3" height="4" rx="0.5"/>
          <rect x="5" y="5" width="3" height="7" rx="0.5"/>
          <rect x="10" y="2" width="3" height="10" rx="0.5"/>
          <rect x="15" y="0" width="3" height="12" rx="0.5"/>
        </svg>
        {/* wifi */}
        <svg width="16" height="12" viewBox="0 0 16 12" fill={color}>
          <path d="M8 11.5 a1.2 1.2 0 1 1 0 -0.1Z"/>
          <path d="M3 6.5 a7 7 0 0 1 10 0 l-1.4 1.4 a5 5 0 0 0 -7.2 0 Z"/>
          <path d="M5.2 8.7 a4 4 0 0 1 5.6 0 l-1.4 1.4 a2 2 0 0 0 -2.8 0 Z"/>
        </svg>
        {/* battery */}
        <svg width="26" height="12" viewBox="0 0 26 12" fill="none" stroke={color}>
          <rect x="0.5" y="0.5" width="22" height="11" rx="2.5" strokeWidth="1"/>
          <rect x="2" y="2" width="19" height="8" rx="1.5" fill={color}/>
          <rect x="23.5" y="3.5" width="2" height="5" rx="0.6" fill={color}/>
        </svg>
      </div>
    </div>
  );
};

// ============================================================
// Top Nav Bar (under status)
// ============================================================
const NavBar = ({ title, theme = 'on-brown', left = 'back', right, onBack, onRight }) => (
  <div className={`cp-nav ${theme}`}>
    <div className="nav-left">
      {left === 'back' && <Icon name="back" size={22}/>}
      {left === 'close' && <Icon name="close" size={22}/>}
      {typeof left === 'object' && left}
    </div>
    <div>{title}</div>
    <div className="nav-right">{right}</div>
  </div>
);

// ============================================================
// TabBar
// ============================================================
const TabBar = ({ active = 'home' }) => {
  const tabs = [
    { key: 'home', label: '首页', icon: 'home' },
    { key: 'dynamic', label: '动态', icon: 'play' },
    { key: 'dist',  label: '分销', icon: 'tree' },
    { key: 'me',    label: '我的', icon: 'user' },
  ];
  return (
    <div className="cp-tabbar">
      {tabs.map(t => (
        <div key={t.key} className={`tab ${t.key === active ? 'active' : ''}`}>
          <div className="ico">
            <Icon name={t.icon} size={22} color={t.key === active ? '#D38A00' : '#9B8E7C'} strokeWidth={1.7}/>
          </div>
          <div>{t.label}</div>
        </div>
      ))}
      <div className="home-indicator"/>
    </div>
  );
};

// ============================================================
// Phone Frame wrapper
// ============================================================
const Phone = ({ children, width = 390, height = 844, bg, dark, dynamicIsland = false, className = '' }) => (
  <div className={`cp-screen ${dark ? 'dark-indicator' : ''} ${className}`} style={{ width, height, background: bg }}>
    {children}
  </div>
);

// ============================================================
// Product image placeholder — colored gradient panel with bottle silhouette
// ============================================================
const ProdImg = ({ kind = 'maotai', text }) => {
  const palettes = {
    maotai:    { bg: 'linear-gradient(135deg, #C4322B 0%, #8B1410 100%)', accent: '#FFD646', label: '飞天' },
    wuliang:   { bg: 'linear-gradient(135deg, #1B2C3D 0%, #0D1825 100%)', accent: '#C9A864', label: '五粮' },
    luzhou:    { bg: 'linear-gradient(135deg, #6B2D1A 0%, #3A1408 100%)', accent: '#E4A516', label: '泸州' },
    yanghe:    { bg: 'linear-gradient(135deg, #1A4878 0%, #0A2747 100%)', accent: '#D8C798', label: '洋河' },
    fenjiu:    { bg: 'linear-gradient(135deg, #E8DCBA 0%, #C9B280 100%)', accent: '#3A2104', label: '汾酒' },
    redwine:   { bg: 'linear-gradient(135deg, #4A0E1A 0%, #1F0509 100%)', accent: '#D6B26A', label: '红酒' },
    whisky:    { bg: 'linear-gradient(135deg, #8B5A1A 0%, #4A2F0A 100%)', accent: '#E8C572', label: '威士忌' },
    gift:      { bg: 'linear-gradient(135deg, #B0381F 0%, #771810 100%)', accent: '#FFD646', label: '礼盒' },
    champagne: { bg: 'linear-gradient(135deg, #3A2A14 0%, #1A1408 100%)', accent: '#E8C572', label: '香槟' },
    sake:      { bg: 'linear-gradient(135deg, #2E2A2A 0%, #1A1818 100%)', accent: '#E8DCBA', label: '清酒' },
  };
  const p = palettes[kind] || palettes.maotai;
  return (
    <div style={{
      width: '100%', height: '100%', background: p.bg,
      position: 'relative', overflow: 'hidden',
      display: 'flex', alignItems: 'flex-end', justifyContent: 'center',
    }}>
      {/* Glow */}
      <div style={{
        position: 'absolute', top: '-20%', left: '50%', transform: 'translateX(-50%)',
        width: '80%', height: '80%', borderRadius: '50%',
        background: `radial-gradient(circle, ${p.accent}22, transparent 70%)`,
      }}/>
      {/* Bottle silhouette */}
      <svg viewBox="0 0 60 100" width="38%" height="80%" style={{ position: 'relative', zIndex: 1 }}>
        <defs>
          <linearGradient id={`bg-${kind}`} x1="0" x2="1">
            <stop offset="0" stopColor={p.accent} stopOpacity="0.9"/>
            <stop offset="0.5" stopColor={p.accent} stopOpacity="1"/>
            <stop offset="1" stopColor={p.accent} stopOpacity="0.7"/>
          </linearGradient>
        </defs>
        <path d="M24 8 H36 V20 L40 28 V92 a4 4 0 0 1 -4 4 H24 a4 4 0 0 1 -4 -4 V28 Z"
              fill={`url(#bg-${kind})`} opacity="0.18"/>
        <rect x="22" y="40" width="16" height="28" rx="1" fill={p.accent} opacity="0.4"/>
        <text x="30" y="56" textAnchor="middle" fontSize="6" fill={p.accent} fontWeight="700">{text || p.label}</text>
        <rect x="22" y="68" width="16" height="6" fill={p.accent} opacity="0.6"/>
      </svg>
      {/* Faint label text */}
      <div style={{
        position: 'absolute', bottom: 8, left: 0, right: 0,
        textAlign: 'center', color: 'rgba(255,255,255,0.25)',
        fontSize: 9, letterSpacing: 2,
      }}>CHUN PIN HUI</div>
    </div>
  );
};

// ============================================================
// Avatar
// ============================================================
const Avatar = ({ size = 44, seed = 'A', kind = 'man1' }) => {
  // generated avatar swatches
  const palettes = {
    man1:   ['#A77F5F', '#5C4632'],
    man2:   ['#7E6240', '#3F2C18'],
    woman1: ['#D49494', '#7E3F3F'],
    woman2: ['#B79968', '#6E5226'],
    boss:   ['#E0BD7F', '#76521A'],
  };
  const [c1, c2] = palettes[kind] || palettes.man1;
  return (
    <div className="avatar" style={{
      width: size, height: size,
      background: `linear-gradient(135deg, ${c1}, ${c2})`,
      fontSize: size * 0.42,
      color: '#fff',
    }}>
      <span style={{ position: 'relative', zIndex: 1 }}>{seed}</span>
    </div>
  );
};

// ============================================================
// Bottom safe indicator for non-tabbar pages
// ============================================================
const HomeIndicator = ({ dark }) => (
  <div className="home-indicator" style={{ background: dark ? 'rgba(255,255,255,0.4)' : 'rgba(0,0,0,0.35)' }}/>
);

// ============================================================
// Brand mark — wine glass in gold
// ============================================================
const BrandMark = ({ size = 80 }) => (
  <div style={{
    width: size, height: size,
    borderRadius: '50%',
    background: 'linear-gradient(135deg, #FFD646 0%, #E4A516 60%, #B07000 100%)',
    boxShadow: '0 8px 28px rgba(228, 165, 22, 0.45), inset 0 -4px 12px rgba(120, 70, 0, 0.4), inset 0 4px 10px rgba(255, 240, 200, 0.5)',
    display: 'flex', alignItems: 'center', justifyContent: 'center',
  }}>
    <svg viewBox="0 0 24 24" width={size * 0.5} height={size * 0.5} fill="none" stroke="#FFFAEB" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
      <path d="M7 3 H17 L16 11 a4 4 0 0 1 -8 0 Z"/>
      <path d="M12 15 V21 M9 21 H15"/>
    </svg>
  </div>
);

// Expose globally
Object.assign(window, { Icon, StatusBar, NavBar, TabBar, Phone, ProdImg, Avatar, HomeIndicator, BrandMark });
