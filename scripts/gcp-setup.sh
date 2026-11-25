#!/usr/bin/env bash
# One-time GCP bootstrap. Edit the variables, then run:  bash scripts/gcp-setup.sh
set -euo pipefail

PROJECT_ID="your-gcp-project"
REGION="europe-north1"            # Finland - closest to Stockholm
CLUSTER="shop-cluster"
AR_REPO="shop"
GITHUB_REPO="YOUR_GH_USER/shop-platform"
SA_NAME="github-deployer"

gcloud config set project "$PROJECT_ID"
gcloud services enable container.googleapis.com artifactregistry.googleapis.com iamcredentials.googleapis.com

# Docker registry
gcloud artifacts repositories create "$AR_REPO" --repository-format=docker --location="$REGION" || true

# GKE Standard cluster with Vertical Pod Autoscaling (HPA is built in; KEDA is installed below)
gcloud container clusters create "$CLUSTER" \
  --region "$REGION" --num-nodes 1 --machine-type e2-standard-4 \
  --enable-vertical-pod-autoscaling \
  --workload-pool="$PROJECT_ID.svc.id.goog"
gcloud container clusters get-credentials "$CLUSTER" --region "$REGION"

# KEDA
helm repo add kedacore https://kedacore.github.io/charts
helm repo update
helm upgrade --install keda kedacore/keda -n keda --create-namespace

# Service account used by GitHub Actions + Workload Identity Federation (no JSON keys)
gcloud iam service-accounts create "$SA_NAME" || true
SA="$SA_NAME@$PROJECT_ID.iam.gserviceaccount.com"
for role in roles/artifactregistry.writer roles/container.developer; do
  gcloud projects add-iam-policy-binding "$PROJECT_ID" --member="serviceAccount:$SA" --role="$role"
done
gcloud iam workload-identity-pools create github --location=global || true
gcloud iam workload-identity-pools providers create-oidc github \
  --location=global --workload-identity-pool=github \
  --issuer-uri="https://token.actions.githubusercontent.com" \
  --attribute-mapping="google.subject=assertion.sub,attribute.repository=assertion.repository" \
  --attribute-condition="assertion.repository=='$GITHUB_REPO'" || true
PROJECT_NUMBER=$(gcloud projects describe "$PROJECT_ID" --format='value(projectNumber)')
gcloud iam service-accounts add-iam-policy-binding "$SA" \
  --role=roles/iam.workloadIdentityUser \
  --member="principalSet://iam.googleapis.com/projects/$PROJECT_NUMBER/locations/global/workloadIdentityPools/github/attribute.repository/$GITHUB_REPO"

echo
echo "Set these in GitHub (Settings > Secrets and variables > Actions):"
echo "  vars:    GCP_PROJECT_ID=$PROJECT_ID  GCP_REGION=$REGION  GKE_CLUSTER=$CLUSTER  GKE_LOCATION=$REGION  AR_REPO=$AR_REPO"
echo "  secrets: WIF_SERVICE_ACCOUNT=$SA"
echo "           WIF_PROVIDER=projects/$PROJECT_NUMBER/locations/global/workloadIdentityPools/github/providers/github"
