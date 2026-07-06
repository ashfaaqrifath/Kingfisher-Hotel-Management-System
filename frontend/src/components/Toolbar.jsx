export default function Toolbar({ search, onSearch, placeholder = 'Search…', children }) {
  return (
    <div className="flex flex-wrap items-center gap-3 mb-4">
      <input
        className="input max-w-xs"
        placeholder={placeholder}
        value={search}
        onChange={(e) => onSearch(e.target.value)}
      />
      {children}
    </div>
  )
}
