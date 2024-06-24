import React from 'react';
import ReactDOM from 'react-dom/client';
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import App from './App.tsx';
import ErrorPage from './pages/ErrorPage.tsx';
import CheckoutPage from './pages/CheckoutPage.tsx';
import PaymentPage from './pages/PaymentPage.tsx';
import ScientistPage from "./pages/ScientistPage.tsx";

import './index.css';

import {FrontendTracerConfig} from './telemetry/FrontendTracer';
import FrontendTracer from './telemetry/FrontendTracer';

const router = createBrowserRouter([
  {
    path: "/",
    element: <App />, // ROOT
    errorElement: <ErrorPage />,
    children:[
      {
        path: "checkout",
        element: <CheckoutPage />,
      },
      {
        path: "payment/:id",
        element: <PaymentPage />,
      },
      {
        path: "scientists",
        element: <ScientistPage />,
      }
    ]
  },
]);

const traceConfig: FrontendTracerConfig = {
  appEnv: 'dev',
  appModule: 'order',
  appName: 'mall-react-app',
  appVersion: '1.0',
  collectorEndpoint: 'http://localhost:14318',
  apiEndpoint: 'http://localhost:9102',
  samplePercentage: 1.0
}

FrontendTracer(traceConfig);

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>,
);
