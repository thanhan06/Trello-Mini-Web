import { Navigate } from "react-router-dom";
import DefaultLayout from "../DefaultLayout/DefaultLayout";
import CategoriesPage from "../pages/CategoriesPage";

export const routes = [
  { path: "/", element: <Navigate to="/categories" replace /> },

  {
    path: "/",
    element: <DefaultLayout />,
    children: [
      { path: "categories", element: <CategoriesPage /> },
    ],
  },
];