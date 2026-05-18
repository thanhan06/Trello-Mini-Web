/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_BASE_URL?: string;
  readonly VITE_GOOGLE_CLIENT_ID?: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}

type GoogleOAuth2CodeResponse = {
  code: string;
  scope?: string;
};

type GoogleOAuth2ErrorResponse = {
  error: string;
  error_description?: string;
  error_uri?: string;
};

type GoogleCodeClientConfig = {
  client_id: string;
  scope: string;
  ux_mode?: 'popup' | 'redirect';
  callback: (response: GoogleOAuth2CodeResponse | GoogleOAuth2ErrorResponse) => void;
};

type GoogleCodeClient = {
  requestCode: () => void;
};

declare global {
  interface Window {
    google?: {
      accounts?: {
        oauth2?: {
          initCodeClient?: (config: GoogleCodeClientConfig) => GoogleCodeClient;
        };
      };
    };
  }
}

export {};