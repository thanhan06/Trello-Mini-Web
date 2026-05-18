import { RouterProvider, createBrowserRouter } from "react-router-dom";
import { routes } from "./routes/routes";
import { ToastProvider } from "./components/Toast";

const router = createBrowserRouter(routes);

function App() {
  return (
    <ToastProvider>
      <RouterProvider router={router} />
    </ToastProvider>
  )
}

export default App
