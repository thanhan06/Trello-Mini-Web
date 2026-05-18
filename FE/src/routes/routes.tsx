import DefaultLayout from "../DefaultLayout/DefaultLayout";
import RegisterPage from "../pages/RegisterPage";
import LoginPage from "../pages/LoginPage";
import HomePage from "../pages/HomePage";

export const routes = [
  {
    path: "/",
    element: <DefaultLayout />,
    children: [
      { index: true, element: <HomePage/> },
      { path: "register", element: <RegisterPage/> },
      { path: "login", element: <LoginPage/> },
    ],
  },
];