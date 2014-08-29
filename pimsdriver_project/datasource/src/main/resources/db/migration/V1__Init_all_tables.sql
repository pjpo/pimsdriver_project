CREATE SCHEMA pmel;
CREATE SCHEMA pmgr;

/* Create functions */
CREATE FUNCTION public.cast_to_int(text, integer) RETURNS INTEGER AS
$$
BEGIN
    RETURN CAST($1 AS integer);
EXCEPTION
    WHEN invalid_text_representation THEN
        RETURN $2;
end;
$$
language plpgsql immutable;

CREATE FUNCTION public.cast_to_bigint(text, bigint) RETURNS BIGINT AS
$$
BEGIN
    RETURN CAST($1 AS bigint);
EXCEPTION
    WHEN invalid_text_representation THEN
        RETURN $2;
end;
$$
language plpgsql immutable;

CREATE FUNCTION public.cast_to_date(text, date) RETURNS DATE AS
$$
BEGIN
	IF char_length($1) = 0 THEN
		RETURN NULL;
	ELSE
		RETURN to_date($1, 'DDMMYYYY');
	END IF;
EXCEPTION
    WHEN invalid_text_representation THEN
        RETURN $2;
    WHEN invalid_datetime_format THEN
    	RETURN $2;
end;
$$
language plpgsql immutable;

CREATE TABLE public.plud_pmsiupload (
  plud_id bigserial NOT NULL,
  plud_finess character varying NOT NULL,
  plud_year integer NOT NULL,
  plud_month smallint NOT NULL,
  plud_dateenvoi timestamp with time zone NOT NULL,
  plud_arguments hstore NOT NULL DEFAULT hstore(''),
  CONSTRAINT plud_pmsiupload_pkey PRIMARY KEY (plud_id)
);

CREATE TABLE public.pmgr_pmsigroups (
  pmgr_id bigserial NOT NULL,
  pmel_root bigint NOT NULL,
  pmel_id bigint NOT NULL,
  pmgr_racine character varying NOT NULL,
  pmgr_modalite character varying NOT NULL,
  pmgr_gravite character varying NOT NULL,
  pmgr_erreur character varying NOT NULL,
  CONSTRAINT pmgr_pmsigroup_pkey PRIMARY KEY (pmgr_id)
);

CREATE TABLE public.pmel_pmsielement (
  pmel_id bigserial NOT NULL,
  pmel_root bigint NOT NULL,
  pmel_position bigint NOT NULL,
  pmel_parent bigint,
  pmel_type character varying NOT NULL,
  pmel_line bigint NOT NULL,
  pmel_content character varying NOT NULL,
  pmel_arguments hstore NOT NULL DEFAULT hstore(''),
  CONSTRAINT pmel_pmsielement_pkey PRIMARY KEY (pmel_id)
);

CREATE TABLE pmel.pmel_cleanup (
  plud_id bigint NOT NULL
);

CREATE VIEW public.fhva_rsfheaders_2012_view AS
SELECT pmel.pmel_id,
       pmel.pmel_root,
       pmel.pmel_position,
       pmel.pmel_parent,
       pmel.pmel_type,
       pmel.pmel_line,
       substring(pmel.pmel_content from 1 for 10) Finess,
       cast_to_bigint(substring(pmel.pmel_content from 10 for 3), NULL) NumLot,
       substring(pmel.pmel_content from 13 for 2) StatutJuridique,
       substring(pmel.pmel_content from 15 for 2) ModeTarifs,
       cast_to_date(substring(pmel.pmel_content from 17 for 8), NULL) DateDebut,
       cast_to_date(substring(pmel.pmel_content from 25 for 8), NULL) DateFin,
       cast_to_bigint(substring(pmel.pmel_content from 33 for 6), NULL) NbEnregistrements,
       cast_to_bigint(substring(pmel.pmel_content from 39 for 6), NULL) NbRSS,
       substring(pmel.pmel_content from 46 for 7) PremierRss,
       substring(pmel.pmel_content from 53 for 7) DernierRss,
       substring(pmel.pmel_content from 60 for 1) DernierEnvoi,
       pmel.pmel_arguments,
       plud.plud_arguments
  FROM public.pmel_pmsielement pmel
  JOIN public.plud_pmsiupload plud ON
    pmel.pmel_root = plud.plud_id
  WHERE pmel.pmel_type = 'rsfheader'
    AND plud.plud_arguments -> 'rsfversion' = 'rsf2012header';
 
CREATE VIEW public.fava_rsfa_2012_view AS
SELECT pmel.pmel_id,
       pmel.pmel_root,
       pmel.pmel_position,
       pmel.pmel_parent,
       pmel.pmel_type,
       pmel.pmel_line,
       'A'::character varying TypeEnregistrement,
       substring(pmel.pmel_content from 2 for 9) Finess,
       substring(pmel.pmel_content from 11 for 20) NumRSS,
       substring(pmel.pmel_content from 31 for 1) Sexe,
       substring(pmel.pmel_content from 32 for 1) CodeCivilite,
       substring(pmel.pmel_content from 33 for 13) CodeSS,
       substring(pmel.pmel_content from 46 for 2) CleCodeSS,
       substring(pmel.pmel_content from 48 for 3) RangBeneficiaire,
       substring(pmel.pmel_content from 51 for 9) NumFacture,
       substring(pmel.pmel_content from 60 for 1) NatureOperation,
       substring(pmel.pmel_content from 61 for 2) NatureAssurance,
       substring(pmel.pmel_content from 63 for 2) TypeContratOC,
       substring(pmel.pmel_content from 65 for 1) JustifExonerationTM,
       substring(pmel.pmel_content from 66 for 1) CodePEC,
       substring(pmel.pmel_content from 67 for 2) CodeGdRegime,
       cast_to_date(substring(pmel.pmel_content from 69 for 8), NULL) DateNaissance,
       substring(pmel.pmel_content from 77 for 1) RangNaissance,
       cast_to_date(substring(pmel.pmel_content from 78 for 8), NULL) DateEntree,
       cast_to_date(substring(pmel.pmel_content from 86 for 8), NULL) DateSortie,
       cast_to_bigint(substring(pmel.pmel_content from 94 for 8), NULL) TotalBaseRemboursementPH,
       cast_to_bigint(substring(pmel.pmel_content from 102 for 8), NULL) TotalRemboursableAMOPH,
       cast_to_bigint(substring(pmel.pmel_content from 110 for 8), NULL) TotalFactureHonoraire,
       cast_to_bigint(substring(pmel.pmel_content from 118 for 8), NULL) TotalRemboursableAMOHonoraire,
       cast_to_bigint(substring(pmel.pmel_content from 126 for 8), NULL) TotalParticipationAvantOC,
       cast_to_bigint(substring(pmel.pmel_content from 134 for 8), NULL) TotalRemboursableOCPH,
       cast_to_bigint(substring(pmel.pmel_content from 142 for 8), NULL) TotalRemboursableOCHonoraire,
       cast_to_bigint(substring(pmel.pmel_content from 150 for 8), NULL) TotalFacturePH,
       substring(pmel.pmel_content from 158 for 1) EtatLiquidation,
       substring(pmel.pmel_content from 159 for 1) CMU,
       substring(pmel.pmel_content from 160 for 9) LienMere,
       pmel.pmel_arguments
  FROM public.pmel_pmsielement pmel
  JOIN public.plud_pmsiupload plud ON
    pmel.pmel_root = plud.plud_id
  WHERE pmel_type = 'rsfa'
    AND plud.plud_arguments -> 'rsfversion' = 'rsf2012header';
    
CREATE VIEW public.favb_rsfb_2012_view AS
SELECT pmel.pmel_id,
       pmel.pmel_root,
       pmel.pmel_position,
       pmel.pmel_parent,
       pmel.pmel_type,
       pmel.pmel_line,
       'B'::character varying TypeEnregistrement,
       substring(pmel.pmel_content from 2 for 9) Finess,
       substring(pmel.pmel_content from 11 for 20) NumRSS,
       substring(pmel.pmel_content from 31 for 13) CodeSS,
       substring(pmel.pmel_content from 44 for 2) CleCodeSS,
       substring(pmel.pmel_content from 46 for 3) RangBeneficiaire,
       substring(pmel.pmel_content from 49 for 9) NumFacture,
       substring(pmel.pmel_content from 58 for 2) ModeTraitement,
       substring(pmel.pmel_content from 60 for 3) DisciplinePrestation,
       cast_to_date(substring(pmel.pmel_content from 63 for 8), NULL) DateDebutSejour,
       cast_to_date(substring(pmel.pmel_content from 71 for 8), NULL) DateFinSejour,
       substring(pmel.pmel_content from 79 for 5) CodeActe,
       cast_to_bigint(substring(pmel.pmel_content from 84 for 3), NULL) Quantite,
       substring(pmel.pmel_content from 87 for 1) JustifExonerationTM,
       substring(pmel.pmel_content from 88 for 5) Coefficient,
       substring(pmel.pmel_content from 93 for 1) CodePEC,
       substring(pmel.pmel_content from 94 for 5) CoefficientMCO,
       cast_to_bigint(substring(pmel.pmel_content from 99 for 7), NULL) PrixUnitaire,
       cast_to_bigint(substring(pmel.pmel_content from 106 for 8), NULL) MontantBaseRemboursementPH,
       substring(pmel.pmel_content from 114 for 3) TauxPrestation,
       cast_to_bigint(substring(pmel.pmel_content from 117 for 8), NULL) MontantRemboursableAMOPH,
       cast_to_bigint(substring(pmel.pmel_content from 125 for 8), NULL) MontantTotalDepense,
       cast_to_bigint(substring(pmel.pmel_content from 133 for 7), NULL) MontantRemboursableOCPH,
       substring(pmel.pmel_content from 140 for 4) NumGHS,
       cast_to_bigint(substring(pmel.pmel_content from 144 for 8), NULL) MontantNOEMIE,
       substring(pmel.pmel_content from 152 for 3) OperationNOEMIE,
       pmel.pmel_arguments
  FROM public.pmel_pmsielement pmel
  JOIN public.plud_pmsiupload plud ON
    pmel.pmel_root = plud.plud_id
  WHERE pmel.pmel_type = 'rsfb'
    AND plud.plud_arguments -> 'rsfversion' = 'rsf2012header';

CREATE VIEW public.favc_rsfc_2012_view AS
SELECT pmel.pmel_id,
       pmel.pmel_root,
       pmel.pmel_position,
       pmel.pmel_parent,
       pmel.pmel_type,
       pmel.pmel_line,
       'C'::character varying TypeEnregistrement,
       substring(pmel.pmel_content from 2 for 9) Finess,
       substring(pmel.pmel_content from 11 for 20) NumRSS,
       substring(pmel.pmel_content from 31 for 13) CodeSS,
       substring(pmel.pmel_content from 44 for 2) CleCodeSS,
       substring(pmel.pmel_content from 46 for 3) RangBeneficiaire,
       substring(pmel.pmel_content from 49 for 9) NumFacture,
       substring(pmel.pmel_content from 58 for 2) ModeTraitement,
       substring(pmel.pmel_content from 60 for 3) DisciplinePrestation,
       substring(pmel.pmel_content from 63 for 1) JustifExonerationTM,
       cast_to_date(substring(pmel.pmel_content from 64 for 8), NULL) DateActe,
       substring(pmel.pmel_content from 72 for 5) CodeActe,
       cast_to_bigint(substring(pmel.pmel_content from 77 for 2), NULL) Quantite,
       substring(pmel.pmel_content from 79 for 6) Coefficient,
       substring(pmel.pmel_content from 85 for 2) Denombrement,
       cast_to_bigint(substring(pmel.pmel_content from 87 for 7), NULL) PrixUnitaire,
       cast_to_bigint(substring(pmel.pmel_content from 94 for 7), NULL) MontantBaseRemboursementHonoraire,
       substring(pmel.pmel_content from 101 for 3) TauxRemboursement,
       cast_to_bigint(substring(pmel.pmel_content from 104 for 7), NULL) MontantRemboursableAMOHonoraire,
       cast_to_bigint(substring(pmel.pmel_content from 111 for 7), NULL) MontantTotalHonoraire,
       cast_to_bigint(substring(pmel.pmel_content from 118 for 6), NULL) MontantRemboursableOCHonoraire,
       cast_to_bigint(substring(pmel.pmel_content from 124 for 8), NULL) MontantNOEMIE,
       substring(pmel.pmel_content from 132 for 3) OperationNOEMIE,
       pmel.pmel_arguments
  FROM public.pmel_pmsielement pmel
  JOIN public.plud_pmsiupload plud ON
    pmel.pmel_root = plud.plud_id
  WHERE pmel.pmel_type = 'rsfc'
    AND plud.plud_arguments -> 'rsfversion' = 'rsf2012header';

CREATE VIEW public.favh_rsfh_2012_view AS
SELECT pmel.pmel_id,
       pmel.pmel_root,
       pmel.pmel_position,
       pmel.pmel_parent,
       pmel.pmel_type,
       pmel.pmel_line,
       'H'::character varying TypeEnregistrement,
       substring(pmel.pmel_content from 2 for 9) Finess,
       substring(pmel.pmel_content from 11 for 20) NumRSS,
       substring(pmel.pmel_content from 31 for 13) CodeSS,
       substring(pmel.pmel_content from 44 for 2) CleCodeSS,
       substring(pmel.pmel_content from 46 for 3) RangBeneficiaire,
       substring(pmel.pmel_content from 49 for 9) NumFacture,
       cast_to_date(substring(pmel.pmel_content from 58 for 8), NULL) DateDebutSejour,
       substring(pmel.pmel_content from 66 for 7) CodeUCD,
       substring(pmel.pmel_content from 73 for 5) CoefficientFractionnement,
       substring(pmel.pmel_content from 78 for 7) PrixAchatUnitaire,
       cast_to_bigint(substring(pmel.pmel_content from 85 for 7), NULL) MontantUnitaireEcartIndemnisable,
       cast_to_bigint(substring(pmel.pmel_content from 92 for 7), NULL) MontantTotalEcartIndemnisable,
       cast_to_bigint(substring(pmel.pmel_content from 99 for 3), NULL) Quantite,
       cast_to_bigint(substring(pmel.pmel_content from 102 for 7), NULL) MontantTotalFactureTTC,
       pmel.pmel_arguments
  FROM public.pmel_pmsielement pmel
  JOIN public.plud_pmsiupload plud ON
    pmel.pmel_root = plud.plud_id
  WHERE pmel.pmel_type = 'rsfh'
    AND plud.plud_arguments -> 'rsfversion' = 'rsf2012header';

CREATE VIEW public.favi_rsfi_2012_view AS
SELECT pmel.pmel_id,
       pmel.pmel_root,
       pmel.pmel_position,
       pmel.pmel_parent,
       pmel.pmel_type,
       pmel.pmel_line,
       'I'::character varying TypeEnregistrement,
       substring(pmel.pmel_content from 2 for 9) Finess,
       substring(pmel.pmel_content from 11 for 20) NumRSS,
       substring(pmel.pmel_content from 31 for 13) CodeSS,
       substring(pmel.pmel_content from 44 for 2) CleCodeSS,
       substring(pmel.pmel_content from 46 for 3) RangBeneficiaire,
       substring(pmel.pmel_content from 49 for 9) NumFacture,
       cast_to_date(substring(pmel.pmel_content from 58 for 8), NULL) DateDebutSejour,
       cast_to_date(substring(pmel.pmel_content from 66 for 8), NULL) DateFinSejour,
       substring(pmel.pmel_content from 74 for 1) NatureFinSejour,
       substring(pmel.pmel_content from 75 for 14) LieuExecutionActe
  FROM public.pmel_pmsielement pmel
  JOIN public.plud_pmsiupload plud ON
    pmel.pmel_root = plud.plud_id
  WHERE pmel.pmel_type = 'rsfi'
    AND plud.plud_arguments -> 'rsfversion' = 'rsf2012header';

CREATE VIEW public.favl_rsfl_2012_view AS
SELECT pmel.pmel_id,
       pmel.pmel_root,
       pmel.pmel_position,
       pmel.pmel_parent,
       pmel.pmel_type,
       pmel.pmel_line,
       'L'::character varying TypeEnregistrement,
       substring(pmel.pmel_content from 2 for 9) Finess,
       substring(pmel.pmel_content from 11 for 20) NumRSS,
       substring(pmel.pmel_content from 31 for 13) CodeSS,
       substring(pmel.pmel_content from 44 for 2) CleCodeSS,
       substring(pmel.pmel_content from 46 for 3) RangBeneficiaire,
       substring(pmel.pmel_content from 49 for 9) NumFacture,
       substring(pmel.pmel_content from 58 for 2) ModeTraitement,
       substring(pmel.pmel_content from 60 for 3) Discipline,
       cast_to_date(substring(pmel.pmel_content from 63 for 8), NULL) DateActe1,
       cast_to_bigint(substring(pmel.pmel_content from 71 for 2), NULL) QuantiteActe1,
       substring(pmel.pmel_content from 73 for 8) CodeActe1,
       cast_to_date(substring(pmel.pmel_content from 81 for 8), NULL) DateActe2,
       cast_to_bigint(substring(pmel.pmel_content from 89 for 2), NULL) QuantiteActe2,
       substring(pmel.pmel_content from 91 for 8) CodeActe2,
       cast_to_date(substring(pmel.pmel_content from 99 for 8), NULL) DateActe3,
       cast_to_bigint(substring(pmel.pmel_content from 107 for 2), NULL) QuantiteActe3,
       substring(pmel.pmel_content from 109 for 8) CodeActe3,
       cast_to_date(substring(pmel.pmel_content from 117 for 8), NULL) DateActe4,
       cast_to_bigint(substring(pmel.pmel_content from 125 for 2), NULL) QuantiteActe4,
       substring(pmel.pmel_content from 127 for 8) CodeActe4,
       cast_to_date(substring(pmel.pmel_content from 135 for 8), NULL) DateActe5,
       cast_to_bigint(substring(pmel.pmel_content from 143 for 2), NULL) QuantiteActe5,
       substring(pmel.pmel_content from 145 for 8) CodeActe5
  FROM public.pmel_pmsielement pmel
  JOIN public.plud_pmsiupload plud ON
    pmel.pmel_root = plud.plud_id
  WHERE pmel.pmel_type = 'rsfl'
    AND plud.plud_arguments -> 'rsfversion' = 'rsf2012header';

CREATE VIEW public.favm_rsfm_2012_view AS
SELECT pmel.pmel_id,
       pmel.pmel_root,
       pmel.pmel_position,
       pmel.pmel_parent,
       pmel.pmel_type,
       pmel.pmel_line,
       'M'::character varying TypeEnregistrement,
       substring(pmel.pmel_content from 2 for 9) Finess,
       substring(pmel.pmel_content from 11 for 20) NumRSS,
       substring(pmel.pmel_content from 31 for 13) CodeSS,
       substring(pmel.pmel_content from 44 for 2) CleCodeSS,
       substring(pmel.pmel_content from 46 for 3) RangBeneficiaire,
       substring(pmel.pmel_content from 49 for 9) NumFacture,
       substring(pmel.pmel_content from 58 for 2) ModeTraitement,
       substring(pmel.pmel_content from 60 for 3) DisciplinePrestation,
       cast_to_date(substring(pmel.pmel_content from 63 for 8), NULL) DateActe,
       substring(pmel.pmel_content from 71 for 13) CodeCCAM,
       substring(pmel.pmel_content from 84 for 1) ExtensionDocumentaire,
       substring(pmel.pmel_content from 85 for 1) Activite,
       substring(pmel.pmel_content from 86 for 1) Phase,
       substring(pmel.pmel_content from 87 for 1) Modificateur1,
       substring(pmel.pmel_content from 88 for 1) Modificateur2,
       substring(pmel.pmel_content from 89 for 1) Modificateur3,
       substring(pmel.pmel_content from 90 for 1) Modificateur4,
       substring(pmel.pmel_content from 91 for 1) AssociationNonPrevue,
       substring(pmel.pmel_content from 92 for 1) CodeRemboursementExceptionnel,
       substring(pmel.pmel_content from 94 for 2) NumDent1,
       substring(pmel.pmel_content from 96 for 2) NumDent2,
       substring(pmel.pmel_content from 98 for 2) NumDent3,
       substring(pmel.pmel_content from 100 for 2) NumDent4,
       substring(pmel.pmel_content from 102 for 2) NumDent5,
       substring(pmel.pmel_content from 104 for 2) NumDent6,
       substring(pmel.pmel_content from 106 for 2) NumDent7,
       substring(pmel.pmel_content from 108 for 2) NumDent8,
       substring(pmel.pmel_content from 110 for 2) NumDent9,
       substring(pmel.pmel_content from 112 for 2) NumDent10,
       substring(pmel.pmel_content from 114 for 2) NumDent11,
       substring(pmel.pmel_content from 116 for 2) NumDent12,
       substring(pmel.pmel_content from 118 for 2) NumDent13,
       substring(pmel.pmel_content from 120 for 2) NumDent14,
       substring(pmel.pmel_content from 122 for 2) NumDent15,
       substring(pmel.pmel_content from 124 for 2) NumDent16
  FROM public.pmel_pmsielement pmel
  JOIN public.plud_pmsiupload plud ON
    pmel.pmel_root = plud.plud_id
  WHERE pmel.pmel_type = 'rsfm'
    AND plud.plud_arguments -> 'rsfversion' = 'rsf2012header';
    
CREATE VIEW public.shva_rssheaders_116_view AS
SELECT pmel.pmel_id,
       pmel.pmel_root,
       pmel.pmel_position,
       pmel.pmel_parent,
       pmel.pmel_type,
       pmel.pmel_line,
       substring(pmel.pmel_content from 1 for 9) Finess,
       substring(pmel.pmel_content from 10 for 3) NumLot,
       substring(pmel.pmel_content from 13 for 2) StatutEtablissement,
       cast_to_date(substring(pmel.pmel_content from 15 for 8), NULL) DbtPeriode,
       cast_to_date(substring(pmel.pmel_content from 23 for 8), NULL) FinPeriode,
       cast_to_bigint(substring(pmel.pmel_content from 31 for 6), NULL) NbEnregistrements,
       cast_to_bigint(substring(pmel.pmel_content from 37 for 6), NULL) NbRSS,
       substring(pmel.pmel_content from 43 for 7) PremierRSS,
       substring(pmel.pmel_content from 50 for 7) DernierRSS,
       substring(pmel.pmel_content from 57 for 1) DernierEnvoiTrimestre
  FROM public.pmel_pmsielement pmel
  JOIN public.plud_pmsiupload plud ON
    pmel.pmel_root = plud.plud_id
  WHERE pmel.pmel_type = 'rssheader'
    AND plud.plud_arguments -> 'rssversion' = 'rss116header';

CREATE VIEW public.smva_rssmain_116_view AS
SELECT pmel.pmel_id,
       pmel.pmel_root,
       pmel.pmel_position,
       pmel.pmel_parent,
       pmel.pmel_type,
       pmel.pmel_line,
       substring(pmel.pmel_content from 1 for 2) VersionClassification,
       substring(pmel.pmel_content from 3 for 2) NumCMD,
       substring(pmel.pmel_content from 5 for 4) NumGHM,
       substring(pmel.pmel_content from 9 for 1) Filler,
       substring(pmel.pmel_content from 10 for 3) VersionFormatRSS,
       substring(pmel.pmel_content from 13 for 3) GroupageCodeRet,
       substring(pmel.pmel_content from 16 for 9) Finess,
       substring(pmel.pmel_content from 25 for 3) VersionFormatRUM,
       substring(pmel.pmel_content from 28 for 20) NumRSS,
       substring(pmel.pmel_content from 48 for 20) NumLocalSejour,
       substring(pmel.pmel_content from 68 for 10) NumRUM,
       cast_to_date(substring(pmel.pmel_content from 78 for 8), NULL) DDN,
       substring(pmel.pmel_content from 86 for 1) Sexe,
       substring(pmel.pmel_content from 87 for 4) NumUniteMedicale,
       substring(pmel.pmel_content from 91 for 2) TypeAutorisationLit,
       cast_to_date(substring(pmel.pmel_content from 93 for 8), NULL) DateEntree,
       substring(pmel.pmel_content from 101 for 1) ModeEntree,
       substring(pmel.pmel_content from 102 for 1) Provenance,
       cast_to_date(substring(pmel.pmel_content from 103 for 8), NULL) DateSortie,
       substring(pmel.pmel_content from 111 for 1) ModeSortie,
       substring(pmel.pmel_content from 112 for 1) Destination,
       substring(pmel.pmel_content from 113 for 5) CPResidence,
       substring(pmel.pmel_content from 118 for 4) PoidsNouveauNe,
       substring(pmel.pmel_content from 122 for 2) AgeGestationnel,
       cast_to_date(substring(pmel.pmel_content from 124 for 8), NULL) DDRegles,
       cast_to_bigint(substring(pmel.pmel_content from 132 for 2), NULL) NbSeances,
       cast_to_bigint(substring(pmel.pmel_content from 134 for 2), NULL) NbDA,
       cast_to_bigint(substring(pmel.pmel_content from 136 for 2), NULL) NbDAD,
       cast_to_bigint(substring(pmel.pmel_content from 138 for 3), NULL) NbZA,
       substring(pmel.pmel_content from 141 for 8) DP,
       substring(pmel.pmel_content from 149 for 8) DR,
       substring(pmel.pmel_content from 157 for 3) IGS2,
       substring(pmel.pmel_content from 160 for 1) ConfCodageRSS,
       substring(pmel.pmel_content from 161 for 1) TypeMachineRadiotherapie,
       substring(pmel.pmel_content from 162 for 1) TypeDosimetrie,
       substring(pmel.pmel_content from 163 for 15) NumInnovation,
       substring(pmel.pmel_content from 178 for 15) ZoneReservee
  FROM public.pmel_pmsielement pmel
  JOIN public.plud_pmsiupload plud ON
    pmel.pmel_root = plud.plud_id
  WHERE pmel.pmel_type = 'rssmain'
    AND plud.plud_arguments -> 'rssversion' = 'rss116header';

CREATE VIEW public.sava_rssacte_116_view AS
SELECT pmel.pmel_id,
       pmel.pmel_root,
       pmel.pmel_position,
       pmel.pmel_parent,
       pmel.pmel_type,
       pmel.pmel_line,
       cast_to_date(substring(pmel.pmel_content from 1 for 8), NULL) DateRealisation,
       substring(pmel.pmel_content from 9 for 7) CodeCCAM,
       substring(pmel.pmel_content from 16 for 1) Phase,
       substring(pmel.pmel_content from 17 for 1) Activite,
       substring(pmel.pmel_content from 18 for 1) Ext,
       substring(pmel.pmel_content from 19 for 4) Modificateurs,
       substring(pmel.pmel_content from 23 for 1) RbtExceptionnel,
       substring(pmel.pmel_content from 24 for 1) AssocNonPrevue,
       cast_to_bigint(substring(pmel.pmel_content from 25 for 2), NULL) NbActe
  FROM public.pmel_pmsielement pmel
  JOIN public.plud_pmsiupload plud ON
    pmel.pmel_root = plud.plud_id
  WHERE pmel.pmel_type = 'rssacte'
    AND plud.plud_arguments -> 'rssversion' = 'rss116header';

CREATE VIEW public.sdva_rssda_116_view AS
SELECT pmel.pmel_id,
       pmel.pmel_root,
       pmel.pmel_position,
       pmel.pmel_parent,
       pmel.pmel_type,
       pmel.pmel_line,
       substring(pmel.pmel_content from 1 for 8) DA
  FROM public.pmel_pmsielement pmel
  JOIN public.plud_pmsiupload plud ON
    pmel.pmel_root = plud.plud_id
  WHERE pmel.pmel_type = 'rssda'
    AND plud.plud_arguments -> 'rssversion' = 'rss116header';
    
CREATE VIEW public.sdda_rssdad_116_view AS
SELECT pmel.pmel_id,
       pmel.pmel_root,
       pmel.pmel_position,
       pmel.pmel_parent,
       pmel.pmel_type,
       pmel.pmel_line,
       substring(pmel.pmel_content from 1 for 8) DAD
  FROM public.pmel_pmsielement pmel
  JOIN public.plud_pmsiupload plud ON
    pmel.pmel_root = plud.plud_id
  WHERE pmel.pmel_type = 'rssdad'
    AND plud.plud_arguments -> 'rssversion' = 'rss116header';
