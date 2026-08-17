# Configurazione Trainly Premium

1. Nel Dashboard Stripe crea un prodotto `Trainly Premium` con un prezzo ricorrente mensile o annuale.
2. Copia l'ID del prezzo (`price_...`) in `STRIPE_PREMIUM_PRICE_ID` nel backend.
3. Configura `STRIPE_SECRET_KEY` e l'origine pubblica del frontend in `FRONTEND_URL`.
4. Esegui `trainly_fe/supabase/billing_schema.sql` nel SQL Editor di Supabase.
5. Crea un endpoint webhook Stripe verso:

   `https://<dominio-backend>/api/v1/billing/webhook`

6. Sottoscrivi questi eventi:

   - `checkout.session.completed`
   - `customer.subscription.created`
   - `customer.subscription.updated`
   - `customer.subscription.deleted`

7. Copia il signing secret dell'endpoint (`whsec_...`) in `STRIPE_WEBHOOK_SECRET`.

Per lo sviluppo locale puoi inoltrare i webhook con Stripe CLI:

```bash
stripe listen --forward-to localhost:8080/api/v1/billing/webhook
```

Usa la chiave `whsec_...` mostrata dal comando. Il frontend non riceve mai chiavi segrete e lo stato Premium viene aggiornato esclusivamente da webhook Stripe firmati.
