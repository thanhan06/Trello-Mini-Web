import { Outlet } from "react-router-dom";

const DefaultLayout = () => {
  return (
    <div>
      {/* header/nav... */}
      <Outlet />
    </div>
  );
};
export default DefaultLayout;