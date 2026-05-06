import { Navigate } from "react-router-dom";
import DefaultLayout from "../DefaultLayout/DefaultLayout";
import RegisterPage from "../pages/RegisterPage";
import LoginPage from "../pages/LoginPage";

export const routes = [
  { path: "/", element: <Navigate to="/register" replace /> },

  {
    path: "/",
    element: <DefaultLayout />,
    children: [
      { path: "register", element: <RegisterPage/>},
      { path: "login", element: <LoginPage/>},

    ],
  },
];