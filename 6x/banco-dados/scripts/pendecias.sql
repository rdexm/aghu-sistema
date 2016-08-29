--19/02/2016 #71270 - Novas coluas VIEW v_mam_receitas
DROP VIEW agh.v_mam_receitas;

-- View: agh.v_mam_receitas
CREATE OR REPLACE VIEW agh.v_mam_receitas AS 
SELECT RCT.CON_NUMERO,
RCT.SEQ,
RCT.TIPO,
RCT.ATD_SEQ,
RCT.ASU_APA_ATD_SEQ,
RCT.DTHR_CRIACAO,
RCT.PAC_CODIGO,
IRC.SEQP,
IRC.DESCRICAO,
IRC.QUANTIDADE,
IRC.FORMA_USO,
IRC.IND_USO_CONTINUO,
IRC.IND_INTERNO
FROM agh.mam_item_receituarios irc,
agh.mam_receituarios rct
WHERE irc.rct_seq = rct.seq AND rct.ind_pendente::text <> 'E'::text AND rct.dthr_mvto IS NULL;

ALTER TABLE agh.v_mam_receitas
OWNER TO postgres;
GRANT ALL ON TABLE agh.v_mam_receitas TO postgres;
GRANT ALL ON TABLE agh.v_mam_receitas TO acesso_completo;
GRANT SELECT ON TABLE agh.v_mam_receitas TO acesso_leitura;


--01/03/2016 #71854 - Ajuste VERSION tabelas IMP
UPDATE agh.IMP_CLASSE_IMPRESSAO SET version = 0 WHERE version is null;
ALTER TABLE agh.IMP_CLASSE_IMPRESSAO ALTER COLUMN version SET DEFAULT 0;
ALTER TABLE agh.IMP_CLASSE_IMPRESSAO ALTER COLUMN version SET NOT NULL;

UPDATE agh.imp_computador SET version = 0 WHERE version is null;
ALTER TABLE agh.imp_computador ALTER COLUMN version SET DEFAULT 0;
ALTER TABLE agh.imp_computador ALTER COLUMN version SET NOT NULL;

UPDATE agh.imp_computador_impressora SET version = 0 WHERE version is null;
ALTER TABLE agh.imp_computador_impressora ALTER COLUMN version SET DEFAULT 0;
ALTER TABLE agh.imp_computador_impressora ALTER COLUMN version SET NOT NULL;

UPDATE agh.imp_impressora SET version = 0 WHERE version is null;
ALTER TABLE agh.imp_impressora ALTER COLUMN version SET DEFAULT 0;
ALTER TABLE agh.imp_impressora ALTER COLUMN version SET NOT NULL;

UPDATE agh.imp_servidor_cups SET version = 0 WHERE version is null;
ALTER TABLE agh.imp_servidor_cups ALTER COLUMN version SET DEFAULT 0;
ALTER TABLE agh.imp_servidor_cups ALTER COLUMN version SET NOT NULL;

--14/04/2016 #74991 - Padronização de campo da unitarizadora.
UPDATE agh.agh_parametros SET vlr_texto = '/opt/aghu/unitarizador/unitarizadora.csv' WHERE nome = 'P_AGHU_CAMINHO_SAIDA_ARQUIVO_UNITARIZADORA';

--25/04/2016 #75322 - Atualizar VIEW v_ael_pesq_pol_exame_unid_hist
CREATE OR REPLACE VIEW agh.v_ael_pesq_pol_exame_unid_hist AS 
SELECT "substring"(unf.descricao::text, 1, 30) AS unf_descricao,
unf.seq AS unf_seq,
CASE
WHEN ise.sit_codigo::text = 'LI'::text THEN ( SELECT max(eis.dthr_evento) AS max
FROM hist.ael_extrato_item_solics eis
WHERE eis.ise_soe_seq = soe.seq AND eis.ise_seqp = ise.seqp AND eis.sit_codigo::text = 'AE'::text)
WHEN ise.sit_codigo::text <> 'LI'::text THEN soe.criado_em
ELSE NULL::timestamp without time zone
END AS data,
oem.ordem_nivel1,
oem.ordem_nivel2,
exa.descricao_usual,
man.descricao,
ise.soe_seq,
ise.seqp,
atd.pac_codigo
FROM agh.agh_atendimentos atd
JOIN hist.ael_solicitacao_exames soe ON atd.seq = soe.atd_seq
JOIN hist.ael_item_solicitacao_exames ise ON soe.seq = ise.soe_seq
JOIN agh.ael_exames exa ON exa.sigla::text = ise.ufe_ema_exa_sigla::text
JOIN agh.ael_materiais_analises man ON man.seq = ise.ufe_ema_man_seq
JOIN agh.agh_unidades_funcionais unf ON unf.seq = ise.ufe_unf_seq
LEFT JOIN agh.ael_ord_exame_mat_analises oem ON oem.ema_exa_sigla::text = ise.ufe_ema_exa_sigla::text AND oem.ema_man_seq = ise.ufe_ema_man_seq
WHERE ise.sit_codigo::text <> 'CA'::text
UNION
SELECT "substring"(unf.descricao::text, 1, 30) AS unf_descricao,
unf.seq AS unf_seq,
CASE
WHEN ise.sit_codigo::text = 'LI'::text THEN ( SELECT max(eis.dthr_evento) AS max
FROM hist.ael_extrato_item_solics eis
WHERE eis.ise_soe_seq = soe.seq AND eis.ise_seqp = ise.seqp AND eis.sit_codigo::text = 'AE'::text)
WHEN ise.sit_codigo::text <> 'LI'::text THEN soe.criado_em
ELSE NULL::timestamp without time zone
END AS data,
oem.ordem_nivel1,
oem.ordem_nivel2,
exa.descricao_usual,
man.descricao,
ise.soe_seq,
ise.seqp,
atv.pac_codigo
FROM agh.ael_atendimento_diversos atv
JOIN hist.ael_solicitacao_exames soe ON atv.seq = soe.atv_seq
JOIN hist.ael_item_solicitacao_exames ise ON soe.seq = ise.soe_seq
JOIN agh.ael_exames exa ON exa.sigla::text = ise.ufe_ema_exa_sigla::text
JOIN agh.ael_materiais_analises man ON man.seq = ise.ufe_ema_man_seq
JOIN agh.agh_unidades_funcionais unf ON unf.seq = ise.ufe_unf_seq
LEFT JOIN agh.ael_ord_exame_mat_analises oem ON oem.ema_exa_sigla::text = ise.ufe_ema_exa_sigla::text AND oem.ema_man_seq = ise.ufe_ema_man_seq
WHERE soe.atv_seq = atv.seq AND ise.soe_seq = soe.seq AND ise.sit_codigo::text <> 'CA'::text AND exa.sigla::text = ise.ufe_ema_exa_sigla::text AND man.seq = ise.ufe_ema_man_seq AND unf.seq = ise.ufe_unf_seq AND oem.ema_exa_sigla::text = ise.ufe_ema_exa_sigla::text AND oem.ema_man_seq = ise.ufe_ema_man_seq
ORDER BY 1, 3 DESC, 4, 5, 6, 7;

--25/04/2016 #75320 - Atualizar VIEW v_ael_pesq_pol_exames_unid
CREATE OR REPLACE VIEW agh.v_ael_pesq_pol_exames_unid AS 
SELECT "substring"(unf.descricao::text, 1, 30) AS unf_descricao,
unf.seq AS unf_seq,
CASE
WHEN ise.sit_codigo::text = 'LI'::text THEN ( SELECT max(eis.dthr_evento) AS max
FROM agh.ael_extrato_item_solics eis
WHERE eis.ise_soe_seq = soe.seq AND eis.ise_seqp = ise.seqp AND eis.sit_codigo::text = 'AE'::text)
WHEN ise.sit_codigo::text <> 'LI'::text THEN soe.criado_em
ELSE NULL::timestamp without time zone
END AS data,
oem.ordem_nivel1,
oem.ordem_nivel2,
exa.descricao_usual,
man.descricao,
ise.soe_seq,
ise.seqp,
atd.pac_codigo
FROM agh.agh_atendimentos atd
JOIN agh.ael_solicitacao_exames soe ON atd.seq = soe.atd_seq
JOIN agh.ael_item_solicitacao_exames ise ON soe.seq = ise.soe_seq
JOIN agh.ael_exames exa ON exa.sigla::text = ise.ufe_ema_exa_sigla::text
JOIN agh.ael_materiais_analises man ON man.seq = ise.ufe_ema_man_seq
JOIN agh.agh_unidades_funcionais unf ON unf.seq = ise.ufe_unf_seq
LEFT JOIN agh.ael_ord_exame_mat_analises oem ON oem.ema_exa_sigla::text = ise.ufe_ema_exa_sigla::text AND oem.ema_man_seq = ise.ufe_ema_man_seq
WHERE ise.sit_codigo::text <> 'CA'::text
UNION
SELECT "substring"(unf.descricao::text, 1, 30) AS unf_descricao,
unf.seq AS unf_seq,
CASE
WHEN ise.sit_codigo::text = 'LI'::text THEN ( SELECT max(eis.dthr_evento) AS max
FROM agh.ael_extrato_item_solics eis
WHERE eis.ise_soe_seq = soe.seq AND eis.ise_seqp = ise.seqp AND eis.sit_codigo::text = 'AE'::text)
WHEN ise.sit_codigo::text <> 'LI'::text THEN soe.criado_em
ELSE NULL::timestamp without time zone
END AS data,
oem.ordem_nivel1,
oem.ordem_nivel2,
exa.descricao_usual,
man.descricao,
ise.soe_seq,
ise.seqp,
atv.pac_codigo
FROM agh.ael_atendimento_diversos atv
JOIN agh.ael_solicitacao_exames soe ON atv.seq = soe.atv_seq
JOIN agh.ael_item_solicitacao_exames ise ON soe.seq = ise.soe_seq
JOIN agh.ael_exames exa ON exa.sigla::text = ise.ufe_ema_exa_sigla::text
JOIN agh.ael_materiais_analises man ON man.seq = ise.ufe_ema_man_seq
JOIN agh.agh_unidades_funcionais unf ON unf.seq = ise.ufe_unf_seq
LEFT JOIN agh.ael_ord_exame_mat_analises oem ON oem.ema_exa_sigla::text = ise.ufe_ema_exa_sigla::text AND oem.ema_man_seq = ise.ufe_ema_man_seq
WHERE soe.atv_seq = atv.seq AND ise.soe_seq = soe.seq AND ise.sit_codigo::text <> 'CA'::text AND exa.sigla::text = ise.ufe_ema_exa_sigla::text AND man.seq = ise.ufe_ema_man_seq AND unf.seq = ise.ufe_unf_seq AND oem.ema_exa_sigla::text = ise.ufe_ema_exa_sigla::text AND oem.ema_man_seq = ise.ufe_ema_man_seq
ORDER BY 1, 3 DESC, 4, 5, 6, 7;

--27/04/2016 #73435 - Aumento de colunas da tabela de JN de consultas.

ALTER TABLE AGH.AAC_CONSULTAS_JN ALTER COLUMN IND_SIT_CONSULTA TYPE CHARACTER VARYING(2);
ALTER TABLE AGH.AAC_CONSULTAS_JN ALTER COLUMN STC_SITUACAO TYPE CHARACTER VARYING(2);

--28/04/2016 #75869 - Criar parâmetro de sistema P_QTD_MAX_RESULTADOS_EXAMES_NAO_VISUALIZADOS
INSERT INTO agh.agh_parametros( seq, sis_sigla, nome, mantem_historico, criado_em, criado_por, alterado_em, alterado_por, vlr_data, vlr_numerico
, vlr_texto, descricao, rotina_consistencia, version, vlr_data_padrao, vlr_numerico_padrao, vlr_texto_padrao, exemplo_uso, tipo_dado) 
VALUES ((select nextval('agh.agh_psi_sq1')), 'MPM', 'P_QTD_MAX_RESULTADOS_EXAMES_NAO_VISUALIZADOS', 'S', now(), 'AGHU', null, null, null, 20, null
, 'Valor numérico que define a quantidade máxima de resultados de exames não visualizados a serem exibidos na prescrição médica', null, 0, null, null
, null, null, 'N');

--09/05/2016 #76018 - Alterar tipo do campo reg_nascimento para String
DROP VIEW agh.v_ain_internacao_paciente;

ALTER TABLE agh.aip_pacientes
ALTER COLUMN reg_nascimento TYPE character varying(32);

ALTER TABLE agh.aip_pacientes_hist
ALTER COLUMN reg_nascimento TYPE character varying(32);

ALTER TABLE agh.agh_responsaveis
ALTER COLUMN reg_nascimento TYPE character varying(32);

ALTER TABLE agh.agh_responsaveis_jn
ALTER COLUMN reg_nascimento TYPE character varying(32);

CREATE OR REPLACE VIEW agh.v_ain_internacao_paciente AS 
SELECT inte.seq,
inte.pac_codigo AS registro,
pac.nome,
pac.prontuario,
pac.nro_cartao_saude AS cartao_sus,
nac.descricao AS nacionalidade,
pac.naturalidade,
pac.grau_instrucao AS cod_escolaridade,
pac.cor AS cod_cor,
pac.rg,
pac.cpf,
pac.numero_pis,
pac.reg_nascimento,
pac.dt_nascimento,
pac.nome_mae,
pac.nome_pai,
pac.sexo,
pac.estado_civil,
ende.logradouro AS "endereço",
ende.nro_logradouro AS numero,
ende.compl_logradouro AS complemento,
ende.bairro,
cid.uf_sigla AS estado,
cid.nome AS cidade,
cid.cep,
endec.logradouro,
pac.ddd_fone_residencial,
pac.fone_residencial AS telefone,
endec.nro_logradouro,
endec.compl_logradouro AS complementoc,
cidc.uf_sigla AS estadoc,
cidc.nome AS cidadec,
cidc.cep AS cepc,
pac.ddd_fone_recado,
pac.fone_recado,
frh.fator_rh,
peso.peso,
alt.altura
FROM agh.ain_internacoes inte
JOIN agh.aip_pacientes pac ON inte.pac_codigo = pac.codigo
JOIN agh.aip_nacionalidades nac ON pac.nac_codigo = nac.codigo
JOIN agh.aip_enderecos_pacientes ende ON pac.codigo = ende.pac_codigo AND ende.tipo_endereco::text = 'R'::text
LEFT JOIN agh.aip_cidades cid ON cid.codigo = ende.cdd_codigo
LEFT JOIN agh.aip_enderecos_pacientes endec ON pac.codigo = endec.pac_codigo AND endec.tipo_endereco::text <> 'R'::text
LEFT JOIN agh.aip_cidades cidc ON cidc.codigo = endec.cdd_codigo
LEFT JOIN agh.aip_paciente_dado_clinicos frh ON pac.codigo = frh.pac_codigo
LEFT JOIN agh.aip_peso_pacientes peso ON pac.codigo = peso.pac_codigo
LEFT JOIN agh.aip_altura_pacientes alt ON pac.codigo = alt.pac_codigo;

ALTER TABLE agh.v_ain_internacao_paciente
OWNER TO postgres;
GRANT ALL ON TABLE agh.v_ain_internacao_paciente TO postgres;
GRANT ALL ON TABLE agh.v_ain_internacao_paciente TO acesso_completo;
GRANT SELECT ON TABLE agh.v_ain_internacao_paciente TO acesso_leitura;

--09/05/2016 #75709 - Permitir apenas que medicos contratados e professor possam ser responsaveis pela cirurgia.
ALTER TABLE AGH.MBC_AGENDAS DROP CONSTRAINT mbc_agd_ck1;

ALTER TABLE AGH.MBC_AGENDAS ADD CONSTRAINT mbc_agd_ck1 CHECK (puc_ind_funcao_prof::text = ANY(ARRAY['MPF'::character varying::text,'MCO'::character varying::text]));

--09/05/2016 #76640 - Insert parametro APAC impressão
insert into agh.agh_parametros values ((select nextval('agh.agh_psi_sq1')),'MAM','P_AGHU_IMPRIMIR_APAC_DIRETO_AMBULATORIO','S',now(),'AGHU',now()
,'AGHU',NULL,NULL,'S','Habilita impressao direta da Apac no ambulatorio desconsiderando regra de impressao e reimpressao faturamento (otorrino, ofta)',NULL,2,NULL
,NULL,NULL,NULL,'T');


--18/05/2016 #67356	 Criação de tabela de Jn de leitos.

CREATE TABLE agh.ain_leitos_jn
(
  jn_user character varying(30) NOT NULL,
  jn_date_time timestamp without time zone NOT NULL,
  jn_operation character varying(3) NOT NULL,
  lto_id character varying(14) NOT NULL,
  qrt_numero smallint NOT NULL,
  leito character varying(4) NOT NULL,
  ind_cons_clin_unidade character varying(1) NOT NULL,
  ind_bloq_leito_limpeza character varying(1) NOT NULL,
  tml_codigo smallint NOT NULL,
  unf_seq smallint NOT NULL,
  ind_situacao character varying(1) DEFAULT 'A'::character varying,
  esp_seq smallint,
  int_seq integer,
  ind_pertence_refl character varying(1) DEFAULT 'S'::character varying,
  ind_cons_esp character varying(1) NOT NULL,
  ser_matricula integer,
  ser_vin_codigo smallint,
  ind_acompanhamento_ccih character varying(1),
  version integer DEFAULT 0,
  seq_jn bigint NOT NULL,
  CONSTRAINT ain_ltoj_pk PRIMARY KEY (seq_jn)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE agh.ain_leitos_jn
  OWNER TO postgres;
GRANT ALL ON TABLE agh.ain_leitos_jn TO postgres;
GRANT ALL ON TABLE agh.ain_leitos_jn TO acesso_completo;
GRANT SELECT ON TABLE agh.ain_leitos_jn TO acesso_leitura;


CREATE SEQUENCE agh.ain_lto_jn_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 99999999999999
  START 1691
  CACHE 2;
ALTER TABLE agh.ain_lto_jn_seq
  OWNER TO postgres;
GRANT ALL ON SEQUENCE agh.ain_lto_jn_seq TO postgres;
GRANT ALL ON SEQUENCE agh.ain_lto_jn_seq TO acesso_completo;
GRANT SELECT ON SEQUENCE agh.ain_lto_jn_seq TO acesso_leitura;

--31/05/2016 #77964 Criaçao de sequence para tabela de PDT
CREATE SEQUENCE agh.pdt_aps_jn_sq1;

--03/06/2016 #78288 - Novo parametro P_AGHU_PERIODO_DEFAULT_VISUALIZAR_CONTROLES
insert into agh.agh_parametros select
(select nextval('agh.agh_psi_sq1')),'ECP','P_AGHU_PERIODO_DEFAULT_VISUALIZAR_CONTROLES','S',now(),'AGHU',NULL,NULL,NULL,24,NULL,
'Define o valor em horas para o período default na tela de visualizar controles de pacientes. Valores aceitáveis: 1, 6, 12, 24, 48, 168 (7 dias), 360 (15 dias)',
NULL,0,NULL,NULL,NULL,NULL,'N'
WHERE
NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_AGHU_PERIODO_DEFAULT_VISUALIZAR_CONTROLES' AND sis_sigla = 'ECP');

--08/06/2016 #78468 - Criação da tabela aac_permissao_agendamento_consultas
CREATE TABLE agh.aac_permissao_agendamento_consultas (
    seq integer NOT NULL,
    ser_matricula integer NOT NULL,
    ser_vin_codigo smallint NOT NULL,
    criado_em timestamp without time zone NOT NULL,
    ind_situacao character varying(1) DEFAULT 'A'::character varying,
    grd_seq integer,
    eqp_seq smallint,
    esp_seq smallint,
    unf_seq smallint,
    prof_ser_matricula integer,
    prof_ser_vin_codigo smallint,
    version integer DEFAULT 0 NOT NULL,
    CONSTRAINT aac_grd_ck1 CHECK (((ind_situacao)::text = ANY (ARRAY[('A'::character varying)::text, ('I'::character varying)::text])))
);

ALTER TABLE ONLY agh.aac_permissao_agendamento_consultas
    ADD CONSTRAINT aac_permissao_agendamento_consultas_pkey PRIMARY KEY (seq);

ALTER TABLE ONLY agh.aac_permissao_agendamento_consultas
    ADD CONSTRAINT aac_perm_ag_con_eqp_fk1 FOREIGN KEY (eqp_seq) REFERENCES agh.agh_equipes(seq);

ALTER TABLE ONLY agh.aac_permissao_agendamento_consultas
    ADD CONSTRAINT aac_perm_ag_con_esp_fk1 FOREIGN KEY (esp_seq) REFERENCES agh.agh_especialidades(seq);

ALTER TABLE ONLY agh.aac_permissao_agendamento_consultas
    ADD CONSTRAINT aac_perm_ag_con_ser_fk1 FOREIGN KEY (ser_matricula, ser_vin_codigo) REFERENCES agh.rap_servidores(matricula, vin_codigo);

ALTER TABLE ONLY agh.aac_permissao_agendamento_consultas
    ADD CONSTRAINT aac_perm_ag_con_prof_ser_fk1 FOREIGN KEY (prof_ser_matricula, prof_ser_vin_codigo) REFERENCES agh.rap_servidores(matricula, vin_codigo);

ALTER TABLE ONLY agh.aac_permissao_agendamento_consultas
    ADD CONSTRAINT aac_perm_ag_con_grd_fk1 FOREIGN KEY (grd_seq) REFERENCES agh.aac_grade_agendamen_consultas(seq) ON DELETE CASCADE;

ALTER TABLE ONLY agh.aac_permissao_agendamento_consultas
    ADD CONSTRAINT aac_perm_ag_con_unf_fk1 FOREIGN KEY (unf_seq) REFERENCES agh.agh_unidades_funcionais(seq);

CREATE SEQUENCE agh.aac_perm_agend_cons_sq1
    START WITH 1
    INCREMENT BY 1
    MAXVALUE 99999999999999
    NO MINVALUE
    CACHE 2;

ALTER TABLE agh.aac_permissao_agendamento_consultas OWNER TO postgres;

GRANT ALL ON TABLE agh.aac_permissao_agendamento_consultas TO postgres;
GRANT ALL ON TABLE agh.aac_permissao_agendamento_consultas TO acesso_completo;
GRANT SELECT ON TABLE agh.aac_permissao_agendamento_consultas TO acesso_leitura;


--13/06/2016 #78846 - Nova coluna tabela agh.agh_unidades_funcionais
alter table agh.agh_unidades_funcionais add column setor character varying(2);

	
--14/06/2016 #78966 - Novo parametro P_CENSO_LEITO_UNICO
INSERT INTO agh.agh_parametros( seq, sis_sigla, nome, mantem_historico, criado_em, criado_por, 
alterado_em, alterado_por, vlr_data, vlr_numerico, 
vlr_texto, descricao, rotina_consistencia, version, vlr_data_padrao, 
vlr_numerico_padrao, vlr_texto_padrao, exemplo_uso, tipo_dado)
select (select nextval('agh.agh_psi_sq1')),'AIN','P_CENSO_LEITO_UNICO','S',now(),'AGHU',
null,null,null,20,
'N','Novo tipo de censo que unifica os movimentos e mostra apenas um leito além disso verifica leitos com movimento fora da janela', null, 0, null,
null,null,null,'N'
WHERE
NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_CENSO_LEITO_UNICO' AND sis_sigla = 'AIN');

--14/06/2016 #78938 - Remover coluna SEQ_PROC_HOSP
alter table agh.AEL_PROCED_SOLICITACAO_EXAMES drop column if exists seq_proc_hosp;
alter table agh.AEL_PROCED_SECUND_SOLICIT_EXAMES drop column if exists seq_proc_hosp;

--15/06/2016 #79013 - Remover constraint ael_hge_ck4
ALTER TABLE agh.ael_horario_grade_exames DROP CONSTRAINT ael_hge_ck4;

--20/06/2016 #79068 Acrescimo de dados na tabela de aplicações

INSERT INTO agh.agh_aplicacoes (codigo, nome, icone, metodo_chamada, ind_impressao, ind_aplic_disp, ambiente, url_aplicacao_aghu, version) VALUES ('AELP_GRAVA_RESULTADO', 'Grava resultados de exames para chamada do Delphi', NULL, 'P', 'N', 'S', 'A', NULL, 0);
INSERT INTO agh.agh_aplicacoes (codigo, nome, icone, metodo_chamada, ind_impressao, ind_aplic_disp, ambiente, url_aplicacao_aghu, version) VALUES ('IMPLAUDO', 'Visualização/Impressão de Resultados e Laudos de Exames', NULL, 'E', 'N', 'S', 'A', NULL, 0);


--04/07/2016 #80452 Evolução da arquitetura / Lentidão

CREATE OR REPLACE VIEW agh.v_afa_prcr_disp_mdtos AS 
 SELECT vpcr.atd_seq,
    vpcr.seq,
    vpcr.dt_referencia,
    vpcr.dthr_inicio,
    vpcr.dthr_fim,
    vpcr.atd_seq_local,
    atendimento.prontuario,
    atendimento.lto_lto_id,
    atendimento.qrt_numero,
    atendimento.unf_seq,
    atendimento.trp_seq,
    paciente.codigo,
    paciente.nome,
    sumarios.apa_atd_seq,
    sumarios.apa_seq,
    sumarios.seqp,
    count(dispmdtossolic.seq) AS countsolic,
    count(dispmdtosdisp.seq) AS countdisp,
    count(dispmdtosconf.seq) AS countconf,
    count(dispmdtosenv.seq) AS countenv,
    count(dispmdtostriado.seq) AS counttriado,
    count(dispmdtosocorr.seq) AS countocorr
   FROM agh.v_afa_prcr_disp vpcr
     LEFT JOIN agh.agh_atendimentos atendimento ON vpcr.atd_seq = atendimento.seq
     LEFT JOIN agh.aip_pacientes paciente ON atendimento.pac_codigo = paciente.codigo
     LEFT JOIN agh.mpm_alta_sumarios sumarios ON vpcr.seq = sumarios.apa_atd_seq AND sumarios.ind_concluido::text = 'S'::text
     LEFT JOIN agh.mpm_prescricao_medicas prescricao ON prescricao.atd_seq = vpcr.atd_seq AND prescricao.seq = vpcr.seq
     LEFT JOIN agh.afa_dispensacao_mdtos dispmdtossolic ON (dispmdtossolic.pme_atd_seq = prescricao.atd_seq AND dispmdtossolic.pme_seq = prescricao.seq OR dispmdtossolic.imo_pmo_pte_atd_seq = prescricao.atd_seq AND dispmdtossolic.imo_pmo_pte_seq = prescricao.seq) AND dispmdtossolic.ind_situacao::text = 'S'::text
     LEFT JOIN agh.afa_dispensacao_mdtos dispmdtosdisp ON (dispmdtosdisp.pme_atd_seq = prescricao.atd_seq AND dispmdtosdisp.pme_seq = prescricao.seq OR dispmdtosdisp.imo_pmo_pte_atd_seq = prescricao.atd_seq AND dispmdtosdisp.imo_pmo_pte_seq = prescricao.seq) AND dispmdtosdisp.ind_situacao::text = 'D'::text
     LEFT JOIN agh.afa_dispensacao_mdtos dispmdtosconf ON (dispmdtosconf.pme_atd_seq = prescricao.atd_seq AND dispmdtosconf.pme_seq = prescricao.seq OR dispmdtosconf.imo_pmo_pte_atd_seq = prescricao.atd_seq AND dispmdtosconf.imo_pmo_pte_seq = prescricao.seq) AND dispmdtosconf.ind_situacao::text = 'C'::text
     LEFT JOIN agh.afa_dispensacao_mdtos dispmdtosenv ON (dispmdtosenv.pme_atd_seq = prescricao.atd_seq AND dispmdtosenv.pme_seq = prescricao.seq OR dispmdtosenv.imo_pmo_pte_atd_seq = prescricao.atd_seq AND dispmdtosenv.imo_pmo_pte_seq = prescricao.seq) AND dispmdtosenv.ind_situacao::text = 'E'::text
     LEFT JOIN agh.afa_dispensacao_mdtos dispmdtostriado ON (dispmdtostriado.pme_atd_seq = prescricao.atd_seq AND dispmdtostriado.pme_seq = prescricao.seq OR dispmdtostriado.imo_pmo_pte_atd_seq = prescricao.atd_seq AND dispmdtostriado.imo_pmo_pte_seq = prescricao.seq) AND dispmdtostriado.ind_situacao::text = 'T'::text AND dispmdtostriado.qtde_dispensada IS NOT NULL AND dispmdtostriado.qtde_dispensada > 0::double precision AND dispmdtostriado.qtde_estornada IS NULL
     LEFT JOIN agh.afa_dispensacao_mdtos dispmdtosocorr ON (dispmdtosocorr.pme_atd_seq = prescricao.atd_seq AND dispmdtosocorr.pme_seq = prescricao.seq OR dispmdtosocorr.imo_pmo_pte_atd_seq = prescricao.atd_seq AND dispmdtosocorr.imo_pmo_pte_seq = prescricao.seq) AND dispmdtosocorr.ind_situacao::text = 'T'::text AND (dispmdtosocorr.qtde_estornada IS NOT NULL AND dispmdtosocorr.qtde_estornada > 0::double precision OR dispmdtosocorr.tod_seq IS NOT NULL)
  GROUP BY vpcr.atd_seq, vpcr.seq, vpcr.dt_referencia, vpcr.dthr_inicio, vpcr.dthr_fim, vpcr.atd_seq_local, atendimento.prontuario, atendimento.lto_lto_id, atendimento.qrt_numero, atendimento.unf_seq, atendimento.trp_seq, sumarios.apa_atd_seq, sumarios.apa_seq, sumarios.seqp, paciente.codigo, paciente.nome;

ALTER TABLE agh.v_afa_prcr_disp_mdtos
  OWNER TO postgres;
GRANT ALL ON TABLE agh.v_afa_prcr_disp_mdtos TO postgres;
GRANT ALL ON TABLE agh.v_afa_prcr_disp_mdtos TO acesso_completo;
GRANT SELECT ON TABLE agh.v_afa_prcr_disp_mdtos TO acesso_leitura;

--01/07/2016 #80105 Criação de parâmetro para verificar o endereço do paciente no ato da marcação de consultas.

INSERT INTO agh.agh_parametros( seq, sis_sigla, nome, mantem_historico, criado_em, criado_por, alterado_em, alterado_por, vlr_data, vlr_numerico
, vlr_texto, descricao, rotina_consistencia, version, vlr_data_padrao, vlr_numerico_padrao, vlr_texto_padrao, exemplo_uso, tipo_dado) 

SELECT
	(select nextval('agh.agh_psi_sq1'))
	,'AAC' -- 'AGENDAMENTO E MARCACAO DE CONSULTAS'
	,'P_VERIFICA_ENDERECO_PACIENTE_MARCACAO_CONSULTA'
	,'S'
	,now()
	,'AGHU'
	,null
	,null
	,null
	,NULL
	,'N'
	,'Parâmetro para verificar o endereço do paciente no ato da marcação de consultas.'
	,null
	,0
	,null
	,null
	,null
	,null
	,'T'
WHERE
	NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_VERIFICA_ENDERECO_PACIENTE_MARCACAO_CONSULTA' AND sis_sigla = 'AAC');

--06/07/2016 #78571 Problema de Desabilitar botões de funcionalidades que não estão disponíveis

INSERT INTO agh.agh_parametros( seq, sis_sigla, nome, mantem_historico, criado_em, criado_por, alterado_em, alterado_por, vlr_data, vlr_numerico, 
vlr_texto, descricao, rotina_consistencia, version, vlr_data_padrao, vlr_numerico_padrao, vlr_texto_padrao, exemplo_uso, tipo_dado) 
SELECT 
	 (select nextval('agh.agh_psi_sq1'))
	,'MPM'
	,'P_DESABILITAR_BOTOES_EXAME_HEMOTERAPIA'
	,'S'
	,now()
	,'AGHU'
	,null
	,null
	,null
	,null
	,'N'
	,'Parametro criado para desabilitar os botões exame e hemoterapia na lista de pacientes no menu de prescrição médica. Valores válidos para o parametro: S = DESABILITADO E N = HABILITADO.'
	,null
	,0
	,null
	,null
	,null
	,null
	,'N'
WHERE
	NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_DESABILITAR_BOTOES_EXAME_HEMOTERAPIA' AND sis_sigla = 'MPM');	

--08/07/2016 #81113 Aumentado o número máximo de caracteres no complemento Prescrição de Enfermagem

ALTER TABLE agh.mpm_prescricao_cuidados ALTER COLUMN descricao TYPE CHARACTER VARYING(240);	
ALTER TABLE agh.epe_prescricoes_cuidados ALTER COLUMN descricao TYPE CHARACTER VARYING(240);

--08/07/2016 #81064 Criação de Parametro  P_EDICAO_DESCRICAO_PHI	

INSERT INTO agh.agh_parametros( seq, sis_sigla, nome, mantem_historico, criado_em, criado_por, alterado_em, alterado_por, vlr_data, vlr_numerico, 
vlr_texto, descricao, rotina_consistencia, version, vlr_data_padrao, vlr_numerico_padrao, vlr_texto_padrao, exemplo_uso, tipo_dado) 
SELECT 
	 (select nextval('agh.agh_psi_sq1'))
	,'FAT'
	,'P_EDICAO_DESCRICAO_PHI'
	,'S'
	,now()
	,'AGHU'
	,null
	,null
	,NULL
	,null
	,'N'
	,'Parâmetro que permite edição de PHI. Valor = S ou N'
	,null
	,0
	,null
	,null
	,NULL
	,null
	,'T'
WHERE
	NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_EDICAO_DESCRICAO_PHI' AND sis_sigla = 'FAT');
	

--21/07/2016 #81369 Aumentou campo descrição na tabela mam_item_anamneses
--Visões vinculadas ao campo
drop view agh.v_mam_item_ana_ident;
drop view agh.v_mam_emg_item_evo_s;
drop view agh.v_mam_emg_item_evo_i;
drop view agh.v_mam_emg_item_evo_c;

ALTER TABLE agh.mam_item_anamneses ALTER COLUMN descricao TYPE text;
ALTER TABLE agh.mam_item_evolucoes ALTER COLUMN descricao TYPE text;

CREATE OR REPLACE VIEW agh.v_mam_item_ana_ident AS
SELECT mam_item_anamneses.ana_seq,
mam_item_anamneses.tin_seq,
mam_item_anamneses.descricao
FROM agh.mam_item_anamneses;

ALTER TABLE agh.v_mam_item_ana_ident
OWNER TO postgres;
GRANT ALL ON TABLE agh.v_mam_item_ana_ident TO postgres;
GRANT ALL ON TABLE agh.v_mam_item_ana_ident TO acesso_completo;
GRANT SELECT ON TABLE agh.v_mam_item_ana_ident TO acesso_leitura;

CREATE OR REPLACE VIEW agh.v_mam_emg_item_evo_s AS
SELECT mam_item_evolucoes.evo_seq,
mam_item_evolucoes.tie_seq,
mam_item_evolucoes.descricao,
1 AS chave
FROM agh.mam_item_evolucoes;

ALTER TABLE agh.v_mam_emg_item_evo_s
OWNER TO postgres;
GRANT ALL ON TABLE agh.v_mam_emg_item_evo_s TO postgres;
GRANT ALL ON TABLE agh.v_mam_emg_item_evo_s TO acesso_completo;
GRANT SELECT ON TABLE agh.v_mam_emg_item_evo_s TO acesso_leitura;

CREATE OR REPLACE VIEW agh.v_mam_emg_item_evo_i AS
SELECT mam_item_evolucoes.evo_seq,
mam_item_evolucoes.tie_seq,
mam_item_evolucoes.descricao,
1 AS chave
FROM agh.mam_item_evolucoes;

ALTER TABLE agh.v_mam_emg_item_evo_i
OWNER TO postgres;
GRANT ALL ON TABLE agh.v_mam_emg_item_evo_i TO postgres;
GRANT ALL ON TABLE agh.v_mam_emg_item_evo_i TO acesso_completo;
GRANT SELECT ON TABLE agh.v_mam_emg_item_evo_i TO acesso_leitura;

CREATE OR REPLACE VIEW agh.v_mam_emg_item_evo_c AS
SELECT mam_item_evolucoes.evo_seq,
mam_item_evolucoes.tie_seq,
mam_item_evolucoes.descricao,
1 AS chave
FROM agh.mam_item_evolucoes;

ALTER TABLE agh.v_mam_emg_item_evo_c
OWNER TO postgres;
GRANT ALL ON TABLE agh.v_mam_emg_item_evo_c TO postgres;
GRANT ALL ON TABLE agh.v_mam_emg_item_evo_c TO acesso_completo;
GRANT SELECT ON TABLE agh.v_mam_emg_item_evo_c TO acesso_leitura;	

-- 02/08/2016 Um parametro foi criado com os valores errados. O script a seguir é uma correção 

UPDATE 
	agh.agh_parametros
SET 
	vlr_numerico = NULL
	,vlr_texto = 'N'
	,tipo_dado = 'T'
WHERE
	nome = 'P_VERIFICA_ENDERECO_PACIENTE_MARCACAO_CONSULTA' 
	AND 
	sis_sigla = 'AAC';

-- 02/08/2016 #81105 Modificação na visão criada para integração do HUSM

DROP VIEW agh.v_integracao;
CREATE OR REPLACE VIEW agh.v_integracao AS 
 SELECT atd.pac_codigo AS pac_id,
    pac.nome AS pac_nome,
    pac.dt_nascimento AS pac_nascimento,
    pac.sexo AS pac_sexo,
    pac.nome_mae AS pac_mae,
    pac.fone_residencial AS pac_fone,
    pac.cpf AS pac_cpf,
    pac.rg AS pac_rg,
    pac.nro_cartao_saude AS pac_cns,
        CASE
            WHEN pac.cor::text = 'B'::text THEN 'Branca'::text
            WHEN pac.cor::text = 'P'::text THEN 'Preta'::text
            WHEN pac.cor::text = 'M'::text THEN 'Parda'::text
            WHEN pac.cor::text = 'A'::text THEN 'Amarela'::text
            WHEN pac.cor::text = 'I'::text THEN 'Indigena'::text
            ELSE 'Sem declaracao'::text
        END AS pac_cor,
        CASE
            WHEN end_pac.bcl_clo_cep IS NOT NULL THEN concat(logr_pac.nome, ', ', end_pac.nro_logradouro)
            ELSE concat(end_pac.logradouro, ', ', end_pac.nro_logradouro)
        END AS pac_endereco,
    end_pac.compl_logradouro AS pac_end_compl,
        CASE
            WHEN end_pac.bcl_clo_cep IS NOT NULL THEN bai_pac.descricao
            ELSE end_pac.bairro
        END AS pac_end_bairro,
        CASE
            WHEN end_pac.bcl_clo_cep IS NOT NULL THEN end_pac.bcl_clo_cep
            ELSE end_pac.cep
        END AS pac_end_cep,
        CASE
            WHEN end_pac.bcl_clo_cep IS NOT NULL THEN cid_pac.nome
            WHEN end_pac.cdd_codigo IS NOT NULL THEN end_cid_pac.nome
            ELSE end_pac.cidade
        END AS pac_end_cidade,
        CASE
            WHEN end_pac.bcl_clo_cep IS NOT NULL THEN cid_pac.uf_sigla
            WHEN end_pac.cdd_codigo IS NOT NULL THEN end_cid_pac.uf_sigla
            ELSE end_pac.uf_sigla
        END AS pac_end_uf,
    cnv.codigo AS exame_conv_id,
    cnv.descricao AS exame_conv_descr,
    solic.unf_seq AS unid_requisitante_id,
    unf_solic.descricao AS unid_requisitante_descr,
    unf_solic.ind_unid_internacao AS und_req_interna,
    unf_exec.unf_seq AS unid_entrega_id,
    unf.descricao AS unid_entrega_descr,
    atd.lto_lto_id AS unid_leito_id,
    qualif.nro_reg_conselho AS solic_reg,
    pessoa.uf_sigla AS solic_uf,
    conselho.sigla AS solic_conselho,
    pessoa.nome AS solic_nome,
    pessoa.dt_nascimento AS solic_nascimento,
    pessoa.sexo AS solic_sexo,
    serv.email AS solic_email,
    item_solic.ufe_ema_exa_sigla AS exame_id,
    exa.descricao AS exame_descr,
    item_solic.ufe_ema_man_seq AS exame_mat_id,
    mat_an.descricao AS exame_descr_mat_analise,
    ex_mat_an.ind_dependente AS exame_dependente,
    tip_am_exa.nro_amostras AS exame_qtde_amostras,
    item_hr.hed_dthr_agenda AS exame_datahora,
    solic.informacoes_clinicas AS exame_info_adic,
    solic.atd_seq AS atendimento_id,
    solic.seq AS solic_exame,
    item_solic.seqp AS solic_seq_item,
    solic.criado_em AS solic_criada_em,
    sit_solic.sit_codigo AS solic_status,
    sit_solic_ex.descricao AS solic_status_descr,
    sit_solic.criado_em AS solic_sit_exame_datahora,
    amo.man_seq AS tipo_am_exa,
    -- >> EXCLUIR
    --amo.seqp AS seq_amostra, 
    -- <<
    item_solic.desc_material_analise,
    -- INCLUIR >>
    atd.prontuario AS pac_same ,
    int_col.descricao AS intervalo_coleta,
    case when int_col.nro_amostras is null then 1 else int_col.nro_amostras end
    -- <<
   FROM agh.ael_solicitacao_exames solic
     JOIN agh.agh_atendimentos atd ON atd.seq = solic.atd_seq
     JOIN agh.ael_item_solicitacao_exames item_solic ON item_solic.soe_seq = solic.seq
     JOIN agh.ael_exames exa ON item_solic.ufe_ema_exa_sigla::text = exa.sigla::text
     JOIN agh.ael_unf_executa_exames unf_exec ON unf_exec.ema_exa_sigla::text = exa.sigla::text AND item_solic.ufe_ema_exa_sigla::text = unf_exec.ema_exa_sigla::text AND item_solic.ufe_ema_man_seq = unf_exec.ema_man_seq AND item_solic.ufe_unf_seq = unf_exec.unf_seq
     JOIN agh.ael_exames_material_analise ex_mat_an ON ex_mat_an.exa_sigla::text = exa.sigla::text AND ex_mat_an.man_seq = item_solic.ufe_ema_man_seq
     JOIN agh.ael_materiais_analises mat_an ON mat_an.seq = ex_mat_an.man_seq
     JOIN agh.agh_unidades_funcionais unf ON unf.seq = unf_exec.unf_seq
     JOIN agh.agh_unidades_funcionais unf_solic ON unf_solic.seq = solic.unf_seq
     LEFT JOIN agh.ael_tipos_amostra_exames tip_am_exa ON mat_an.seq = tip_am_exa.man_seq AND ex_mat_an.exa_sigla::text = tip_am_exa.ema_exa_sigla::text AND ex_mat_an.man_seq = tip_am_exa.ema_man_seq
     LEFT JOIN agh.ael_item_horario_agendados item_hr ON item_hr.ise_soe_seq = item_solic.soe_seq AND item_hr.ise_seqp = item_solic.seqp
     JOIN agh.rap_servidores serv ON solic.ser_matricula = serv.matricula
     JOIN agh.rap_pessoas_fisicas pessoa ON pessoa.codigo = serv.pes_codigo
     LEFT JOIN agh.rap_qualificacoes qualif ON qualif.pes_codigo = pessoa.codigo AND qualif.sequencia = (( SELECT max(q.sequencia) AS max
           FROM agh.rap_qualificacoes q
          WHERE q.pes_codigo = qualif.pes_codigo))
     LEFT JOIN agh.rap_tipos_qualificacao tipo_qualif ON qualif.tql_codigo = tipo_qualif.codigo
     LEFT JOIN agh.rap_conselhos_profissionais conselho ON tipo_qualif.cpr_codigo = conselho.codigo
     JOIN agh.fat_conv_saude_planos cnv_planos ON cnv_planos.seq = solic.csp_seq AND cnv_planos.cnv_codigo = solic.csp_cnv_codigo
     JOIN agh.fat_convenios_saude cnv ON cnv.codigo = cnv_planos.cnv_codigo
     JOIN agh.aip_pacientes pac ON atd.pac_codigo = pac.codigo
     JOIN agh.aip_enderecos_pacientes end_pac ON end_pac.pac_codigo = pac.codigo
     LEFT JOIN agh.aip_cidades end_cid_pac ON end_cid_pac.codigo = end_pac.cdd_codigo
     LEFT JOIN agh.aip_bairros bai_pac ON end_pac.bcl_bai_codigo = bai_pac.codigo
     LEFT JOIN agh.aip_logradouros logr_pac ON end_pac.bcl_clo_lgr_codigo = logr_pac.codigo
     LEFT JOIN agh.aip_cidades cid_pac ON logr_pac.cdd_codigo = cid_pac.codigo
     JOIN agh.ael_sit_item_solicitacoes sit_solic_ex ON sit_solic_ex.codigo::text = item_solic.sit_codigo::text
     JOIN agh.ael_extrato_item_solics sit_solic ON item_solic.soe_seq = sit_solic.ise_soe_seq AND item_solic.seqp = sit_solic.ise_seqp AND sit_solic.seqp = (( SELECT max(es1.seqp) AS max
           FROM agh.ael_extrato_item_solics es1
          WHERE sit_solic.ise_soe_seq = es1.ise_soe_seq AND sit_solic.ise_seqp = es1.ise_seqp))
     -- >> ALTERAR
     LEFT JOIN agh.ael_amostra_item_exames am_item_ex ON item_solic.soe_seq = am_item_ex.ise_soe_seq AND item_solic.seqp = am_item_ex.ise_seqp and am_item_ex.amo_seqp = 1
     -- <<
     LEFT JOIN agh.ael_amostras amo ON am_item_ex.amo_soe_seq = amo.soe_seq AND am_item_ex.amo_seqp = amo.seqp
     -- >> INCLUIR
     LEFT JOIN (select vic.seq, vic.descricao, vic.tipo_substancia, vic.ema_exa_sigla, vic.ema_man_seq, COUNT(vic.SEQ) nro_amostras
                from agh.v_ael_intervalo_coletas vic 
                GROUP BY vic.seq, vic.descricao, vic.tipo_substancia, vic.ema_exa_sigla, vic.ema_man_seq) int_col  on item_solic.ico_seq = int_col.seq
     -- <<
     -- >> EXCLUIR
     -- LEFT JOIN agh.ael_materiais_analises mat_an_am ON mat_an_am.seq = amo.man_seq 
     -- <<
ORDER BY solic.seq DESC, item_solic.seqp;

ALTER TABLE agh.v_integracao
  OWNER TO postgres;
GRANT ALL ON TABLE agh.v_integracao TO postgres;
GRANT SELECT ON TABLE agh.v_integracao TO ugen_integra_exames;	

--08/08/2016 #82724 Criação de view e adição de paramêtros.


INSERT INTO agh.agh_parametros(seq, sis_sigla, nome, mantem_historico, criado_em, criado_por, alterado_em, alterado_por, vlr_data, vlr_numerico, vlr_texto,
descricao, rotina_consistencia, version, vlr_data_padrao, vlr_numerico_padrao, vlr_texto_padrao, exemplo_uso, tipo_dado)
VALUES ((select nextval('agh.agh_psi_sq1')), 'AGH', 'PME_LISTA_PACIENTES_USAR_BUSCA_OTIMIZADA', 'N', now(), 'AGHU', now(), 'AGHU', null, NULL, 'N',
'Parametro para indicar se a tela de lista de pacientes da prescricao medica busca pelo SQL nativo otimizado', null, 0, null, null, 'S', null, 'T');

-- View: agh.v_mpm_lista_pac_internados
DROP VIEW agh.v_mpm_lista_pac_internados;
CREATE OR REPLACE VIEW agh.v_mpm_lista_pac_internados AS 
 SELECT atd.seq AS atd_seq,
    atd.prontuario,
    atd.pac_codigo,
    pac.nome,
    pac.nome_social,
    atd.dthr_inicio AS data_inicio_atendimento,
    atd.dthr_fim AS data_fim_atendimento,
    pac.dt_nascimento AS data_nascimento,
    atd.ser_matricula AS atd_ser_matricula,
    atd.ser_vin_codigo AS atd_ser_vin_codigo,
    atd.ser_matricula,
    atd.ser_vin_codigo,
    atd.ind_sit_sumario_alta,
    atd.origem,
    atd.ESP_SEQ,
    atd.UNF_SEQ,
    atd.ind_pac_cpa,
    atd.IND_PAC_ATENDIMENTO,
    atd.LTO_LTO_ID,
    atd.QRT_NUMERO,
    (case when rapf.nome_usual is null or rapf.nome_usual = '' then rapf.nome else rapf.nome_usual end) as nome_responsavel
    ,   CASE
            WHEN atd.origem::text = 'I'::text AND (( SELECT max(cpa.criado_em) AS max
               FROM agh.mpm_control_prev_altas cpa
              WHERE cpa.atd_seq = atd.seq AND cpa.resposta::text = 'S'::text AND cpa.dt_fim IS NOT NULL AND date_part('day'::text, cpa.dt_fim::timestamp with time zone - now()) >= 0::double precision AND date_part('day'::text, cpa.dt_fim::timestamp with time zone - now()) <= 2::double precision)) IS NOT NULL THEN 'true'::text
            WHEN atd.origem::text = 'N'::text AND (( SELECT max(cpa.criado_em) AS max
               FROM agh.mpm_control_prev_altas cpa
                 JOIN agh.agh_atendimentos atd_mae ON atd_mae.seq = cpa.atd_seq AND atd_mae.origem::text = 'I'::text
              WHERE cpa.atd_seq = atd.atd_seq_mae AND cpa.resposta::text = 'S'::text AND cpa.dt_fim IS NOT NULL AND date_part('day'::text, cpa.dt_fim::timestamp with time zone - now()) >= 0::double precision AND date_part('day'::text, cpa.dt_fim::timestamp with time zone - now()) <= 2::double precision)) IS NOT NULL THEN 'true'::text
            ELSE 'false'::text
        END AS possui_plano_altas,
        CASE
            WHEN atd.lto_lto_id IS NOT NULL THEN concat('L:', atd.lto_lto_id)
            WHEN atd.qrt_numero IS NOT NULL THEN concat('Q:', atd.qrt_numero)
            ELSE ( SELECT (((('U:'::text || unf_1.andar::text) || ' '::text) || unf_1.ind_ala::text) || ' - '::text) || unf_1.descricao::text
               FROM agh.agh_unidades_funcionais unf_1
              WHERE unf_1.seq = atd.unf_seq)
        END AS local,
        CASE
            WHEN
            CASE
                WHEN atd.ind_pac_atendimento::text = 'S'::text AND cuf.unf_seq IS NOT NULL THEN 'false'::text
                ELSE 'true'::text
            END = 'true'::text THEN ''::text
            WHEN NOT (( SELECT count(*) AS count
               FROM agh.mpm_prescricao_medicas pm
              WHERE pm.atd_seq = atd.seq AND pm.ser_matricula_valida IS NOT NULL AND pm.ser_vin_codigo_valida IS NOT NULL AND pm.dthr_fim > now())) > 0 THEN 'PRESCRICAO_NAO_REALIZADA'::text
            WHEN NOT (( SELECT count(*) AS count
               FROM agh.mpm_prescricao_medicas pm
              WHERE pm.atd_seq = atd.seq AND pm.ser_matricula_valida IS NOT NULL AND pm.ser_vin_codigo_valida IS NOT NULL AND pm.dthr_inicio > now() AND to_char(unf.hrio_validade_pme, 'HH24:mi'::text) = to_char(pm.dthr_inicio, 'HH24:mi'::text))) > 0 THEN 'PRESCRICAO_VENCE_NO_DIA'::text
            ELSE 'PRESCRICAO_VENCE_NO_DIA_SEGUINTE'::text
        END AS status_prescricao,
        CASE
            WHEN atd.ind_pac_atendimento::text = 'N'::text AND atd.ctrl_sumr_alta_pendente::text = 'E'::text THEN 'SUMARIO_ALTA_NAO_ENTREGUE_SAMIS'::text
            WHEN atd.ind_pac_atendimento::text = 'N'::text AND atd.ctrl_sumr_alta_pendente::text <> 'E'::text THEN 'SUMARIO_ALTA_NAO_REALIZADO'::text
            ELSE ''::text
        END AS status_sumario_alta,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.ael_item_solicitacao_exames ise
                 JOIN agh.ael_solicitacao_exames soe ON ise.soe_seq = soe.seq
              WHERE soe.atd_seq = atd.seq AND ise.sit_codigo::text = 'LI'::text AND NOT (EXISTS ( SELECT its.ise_seqp,
                        its.ise_soe_seq
                       FROM agh.ael_item_solic_consultados its
                         JOIN agh.ael_item_solicitacao_exames sub_ise ON its.ise_seqp = sub_ise.seqp AND its.ise_soe_seq = sub_ise.soe_seq
                         JOIN agh.ael_solicitacao_exames sub_soe ON sub_ise.soe_seq = sub_soe.seq
                      WHERE its.ise_soe_seq = ise.soe_seq AND its.ise_seqp = ise.seqp AND sub_soe.atd_seq = atd.seq AND sub_ise.sit_codigo::text = 'LI'::text)) AND NOT (EXISTS ( SELECT iri.ise_seqp,
                        iri.ise_soe_seq
                       FROM agh.ael_itens_resul_impressao iri
                      WHERE iri.ise_soe_seq = ise.soe_seq AND iri.ise_seqp = ise.seqp)))) > 0 THEN 'RESULTADOS_NAO_VISUALIZADOS'::text
            WHEN (( SELECT count(*) AS count
               FROM agh.ael_item_solicitacao_exames ise
                 JOIN agh.ael_solicitacao_exames soe ON ise.soe_seq = soe.seq
              WHERE soe.atd_seq = atd.seq AND ise.sit_codigo::text = 'LI'::text AND NOT (EXISTS ( SELECT its.ise_seqp,
                        its.ise_soe_seq
                       FROM agh.ael_item_solic_consultados its
                         JOIN agh.ael_item_solicitacao_exames sub_ise ON its.ise_seqp = sub_ise.seqp AND its.ise_soe_seq = sub_ise.soe_seq
                         JOIN agh.ael_solicitacao_exames sub_soe ON sub_ise.soe_seq = sub_soe.seq
                      WHERE its.ise_soe_seq = ise.soe_seq AND its.ise_seqp = ise.seqp AND sub_soe.atd_seq = atd.seq AND sub_ise.sit_codigo::text = 'LI'::text AND its.ser_matricula = atd.ser_matricula AND its.ser_vin_codigo = atd.ser_vin_codigo)) AND NOT (EXISTS ( SELECT iri.ise_seqp,
                        iri.ise_soe_seq
                       FROM agh.ael_itens_resul_impressao iri
                      WHERE iri.ise_soe_seq = ise.soe_seq AND iri.ise_seqp = ise.seqp)))) > 0 THEN 'RESULTADOS_VISUALIZADOS_OUTRO_MEDICO'::text
            ELSE ''::text
        END AS status_exames_nao_vistos,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.mpm_laudos lau
              WHERE lau.atd_seq = atd.seq AND lau.tuo_seq IS NOT NULL AND lau.justificativa IS NULL)) > 0 THEN 'PENDENCIA_LAUDO_UTI'::text
            ELSE ''::text
        END AS status_pendencia_documento,
        CASE
            WHEN (( SELECT count(projetos.pac_codigo) AS count
               FROM agh.ael_projeto_pacientes projetos
              WHERE projetos.pac_codigo = atd.pac_codigo AND (projetos.dt_fim IS NULL OR projetos.dt_fim >= now()) AND (projetos.jex_seq IS NULL OR (EXISTS ( SELECT jex_.seq AS y0_
                       FROM agh.ael_justificativa_exclusoes jex_
                      WHERE jex_.seq = projetos.jex_seq AND jex_.ind_mostra_telas::text = 'S'::text))) AND (EXISTS ( SELECT pjq_.seq AS y0_
                       FROM agh.ael_projeto_pesquisas pjq_
                      WHERE pjq_.seq = projetos.pjq_seq AND (pjq_.dt_fim IS NULL OR pjq_.dt_fim >= now()))))) > 0 THEN 'PACIENTE_PESQUISA'::text
            ELSE ''::text
        END AS status_paciente_pesquisa,
        CASE
            WHEN atd.origem::text = 'I'::text OR atd.origem::text = 'N'::text THEN
            CASE
                WHEN (( SELECT count(*) AS count
                   FROM agh.mam_evolucoes evo
                  WHERE evo.atd_seq = atd.seq AND date_part('day'::text, evo.dthr_valida::timestamp with time zone - now()) = 0::double precision AND evo.dthr_valida_mvto IS NULL)) > 0 THEN
                CASE
                    WHEN (( SELECT cprf.cag_seq AS seq
                       FROM agh.cse_categoria_perfis cprf
                         JOIN agh.cse_categoria_profissionais csecategor1_ ON cprf.cag_seq = csecategor1_.seq
                      WHERE csecategor1_.ind_situacao::text = 'A'::text AND cprf.ind_situacao::text = 'A'::text AND (cprf.per_nome::text IN ( SELECT cprf_1.nome AS y0_
                               FROM casca.csc_perfil cprf_1
                                 JOIN casca.csc_perfis_usuarios perfisusua1_ ON cprf_1.id = perfisusua1_.id_perfil
                                 JOIN casca.csc_usuario usuario2_ ON perfisusua1_.id_usuario = usuario2_.id
                                 JOIN agh.rap_servidores srv ON lower(srv.usuario::text) = lower(usuario2_.login::text)
                              WHERE cprf_1.situacao::text = 'A'::text AND (perfisusua1_.dthr_expiracao IS NULL OR perfisusua1_.dthr_expiracao > now()) AND usuario2_.ativo = true AND srv.matricula = atd.ser_matricula AND srv.vin_codigo = atd.ser_vin_codigo))
                     LIMIT 1)) = (( SELECT agh_parametros.vlr_numerico
                       FROM agh.agh_parametros
                      WHERE agh_parametros.nome::text = 'P_CATEG_PROF_MEDICO'::text)) THEN 'EVOLUCAO'::text
                    WHEN (( SELECT cprf.cag_seq AS seq
                       FROM agh.cse_categoria_perfis cprf
                         JOIN agh.cse_categoria_profissionais csecategor1_ ON cprf.cag_seq = csecategor1_.seq
                      WHERE csecategor1_.ind_situacao::text = 'A'::text AND cprf.ind_situacao::text = 'A'::text AND (cprf.per_nome::text IN ( SELECT cprf_1.nome AS y0_
                               FROM casca.csc_perfil cprf_1
                                 JOIN casca.csc_perfis_usuarios perfisusua1_ ON cprf_1.id = perfisusua1_.id_perfil
                                 JOIN casca.csc_usuario usuario2_ ON perfisusua1_.id_usuario = usuario2_.id
                                 JOIN agh.rap_servidores srv ON lower(srv.usuario::text) = lower(usuario2_.login::text)
                              WHERE cprf_1.situacao::text = 'A'::text AND (perfisusua1_.dthr_expiracao IS NULL OR perfisusua1_.dthr_expiracao > now()) AND usuario2_.ativo = true AND srv.matricula = atd.ser_matricula AND srv.vin_codigo = atd.ser_vin_codigo))
                     LIMIT 1)) = (( SELECT agh_parametros.vlr_numerico
                       FROM agh.agh_parametros
                      WHERE agh_parametros.nome::text = 'P_CATEG_PROF_OUTROS'::text)) THEN 'EVOLUCAO'::text
                    WHEN (( SELECT cprf.cag_seq AS seq
                       FROM agh.cse_categoria_perfis cprf
                         JOIN agh.cse_categoria_profissionais csecategor1_ ON cprf.cag_seq = csecategor1_.seq
                      WHERE csecategor1_.ind_situacao::text = 'A'::text AND cprf.ind_situacao::text = 'A'::text AND (cprf.per_nome::text IN ( SELECT cprf_1.nome AS y0_
                               FROM casca.csc_perfil cprf_1
                                 JOIN casca.csc_perfis_usuarios perfisusua1_ ON cprf_1.id = perfisusua1_.id_perfil
                                 JOIN casca.csc_usuario usuario2_ ON perfisusua1_.id_usuario = usuario2_.id
                                 JOIN agh.rap_servidores srv ON lower(srv.usuario::text) = lower(usuario2_.login::text)
                              WHERE cprf_1.situacao::text = 'A'::text AND (perfisusua1_.dthr_expiracao IS NULL OR perfisusua1_.dthr_expiracao > now()) AND usuario2_.ativo = true AND srv.matricula = atd.ser_matricula AND srv.vin_codigo = atd.ser_vin_codigo))
                     LIMIT 1)) = (( SELECT agh_parametros.vlr_numerico
                       FROM agh.agh_parametros
                      WHERE agh_parametros.nome::text = 'P_CATEG_PROF_ENF'::text)) THEN
                    CASE
                        WHEN (( SELECT count(*) AS count
                           FROM agh.mam_item_evolucoes iev
                          WHERE iev.evo_seq = (( SELECT evo.seq
                                   FROM agh.mam_evolucoes evo
                                  WHERE evo.atd_seq = atd.seq AND date_part('day'::text, evo.dthr_valida::timestamp with time zone - now()) = 0::double precision AND evo.dthr_valida_mvto IS NULL
                                 LIMIT 1)) AND (iev.tie_seq IN ( SELECT mam_tipo_item_evolucoes.seq
                                   FROM agh.mam_tipo_item_evolucoes
                                  WHERE mam_tipo_item_evolucoes.sigla::text = 'C'::text)))) > 0 THEN 'EVOLUCAO'::text
                        ELSE ''::text
                    END
                    ELSE ''::text
                END
                ELSE ''::text
            END
            ELSE ''::text
        END AS status_evolucao,
        CASE
            WHEN (( SELECT count(docs.pac_codigo) AS count
               FROM agh.v_agh_versoes_documentos docs
              WHERE docs.dov_situacao::text = 'P'::text AND docs.pac_codigo = atd.pac_codigo)) > 0 THEN 'PENDENTE'::text
            ELSE ''::text
        END AS status_certificacao_digital,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.mpm_sumario_altas sa
              WHERE sa.atd_seq = atd.seq AND sa.mam_seq IS NOT NULL)) > 0 THEN 'true'::text
            WHEN (( SELECT count(*) AS count
               FROM agh.mpm_alta_sumarios asu
              WHERE asu.apa_atd_seq = atd.seq AND asu.ind_concluido::text = 'S'::text)) > 0 THEN 'true'::text
            WHEN ((( SELECT agh_parametros.vlr_texto
               FROM agh.agh_parametros
              WHERE agh_parametros.nome::text = 'P_BLOQUEIA_PAC_EMERG'::text))::text) <> 'S'::text AND (( SELECT count(*) AS count
               FROM agh.agh_caract_unid_funcionais cuf2
              WHERE cuf2.unf_seq = atd.unf_seq AND cuf2.caracteristica::text = 'Atend emerg terreo'::text)) > 0 THEN 'true'::text
            ELSE 'false'::text
        END AS disable_button_alta_obito,
        CASE
            WHEN atd.ind_pac_atendimento::text = 'S'::text AND cuf.unf_seq IS NOT NULL THEN 'false'::text
            ELSE 'true'::text
        END AS disable_button_prescrever,
        CASE
            WHEN atd.ind_pac_atendimento::text = 'N'::text AND atd.ctrl_sumr_alta_pendente::text <> 'E'::text THEN 'SUMARIO_ALTA'::text
            ELSE 'ALTA'::text
        END AS label_alta,
        CASE
            WHEN atd.ind_pac_atendimento::text = 'N'::text AND atd.ctrl_sumr_alta_pendente::text <> 'E'::text THEN 'SUMARIO_OBITO'::text
            ELSE 'OBITO'::text
        END AS label_obito,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.mci_notificacao_gmr gmr
              WHERE gmr.pac_codigo = atd.pac_codigo AND gmr.ind_notificacao_ativa::text = 'S'::text)) > 0 THEN 'true'::text
            ELSE 'false'::text
        END AS ind_gmr,
	case 
	   when (select count(*) from agh.AGH_CARACT_UNID_FUNCIONAIS car
		where car.caracteristica = 'Anamnese/Evolução Eletrônica'
		and car.unf_seq = atd.unf_seq) > 0 then 'true'::text
		else 'false'::text
	end as TEM_UNF_CARACT_ANAMNESE_EVOLUCAO,
	case 
	   when (select count(*) 
		from agh.MPM_ANAMNESES ana
		   inner join agh.AGH_ATENDIMENTOS atd1 on atd1.seq = ana.atd_seq
		      inner join agh.RAP_SERVIDORES rap on rap.matricula = atd1.ser_matricula and rap.vin_codigo = atd1.ser_vin_codigo
		         inner join agh.RAP_PESSOAS_FISICAS pes on pes.codigo = rap.pes_codigo
		      inner join agh.AGH_UNIDADES_FUNCIONAIS unf on unf.seq = atd1.UNF_SEQ
		         inner join agh.AGH_CARACT_UNID_FUNCIONAIS car on car.unf_seq = unf.seq
		where ana.atd_seq = atd.seq) > 0 
	    then (
		select distinct case ana.IND_PENDENTE
			when 'R' then 'ANAMNESE_NAO_REALIZADA'
			when 'P' then 'ANAMNESE_PENDENTE'
			when 'V' then
			    case 
			       when (select count(*)
					from agh.MPM_EVOLUCOES evo
					inner join agh.MPM_ANAMNESES ana on ana.seq = evo.ana_seq
					where evo.ANA_SEQ = ana.seq
					and to_char(evo.DTHR_REFERENCIA, 'YYYYMMDD') = to_char(now(), 'YYYYMMDD') 
					and evo.IND_PENDENTE <> 'R') = 0 then 'EVOLUCAO_DO_DIA_NAO_REALIZADA'
			       when (select count(*)
					from agh.MPM_EVOLUCOES evo
					inner join agh.MPM_ANAMNESES ana on ana.seq = evo.ana_seq
					where evo.ANA_SEQ = ana.seq
					and to_char(evo.DTHR_REFERENCIA, 'YYYYMMDD') = to_char(now(), 'YYYYMMDD') 
					and evo.IND_PENDENTE = 'P') > 0 then 'EVOLUCAO_DO_DIA_PENDENTE'
			       when (select count(*)
					from agh.MPM_EVOLUCOES evo
					inner join agh.MPM_ANAMNESES ana on ana.seq = evo.ana_seq
					where evo.ANA_SEQ = ana.seq
					and evo.DTHR_REFERENCIA > date_trunc('day', now()) 
					and evo.IND_PENDENTE = 'V') = 0 then 'EVOLUCAO_VENCE_NO_DIA_SEGUINTE'
				else null
				end
			else null
			end 
		from agh.MPM_ANAMNESES ana
		   inner join agh.AGH_ATENDIMENTOS atd1 on atd1.seq = ana.atd_seq
		      inner join agh.RAP_SERVIDORES rap on rap.matricula = atd1.ser_matricula and rap.vin_codigo = atd1.ser_vin_codigo
			 inner join agh.RAP_PESSOAS_FISICAS pes on pes.codigo = rap.pes_codigo
		      inner join agh.AGH_UNIDADES_FUNCIONAIS unf1 on unf1.seq = atd1.UNF_SEQ
			 inner join agh.AGH_CARACT_UNID_FUNCIONAIS car on car.unf_seq = unf1.seq
		where ana.atd_seq = atd.seq)::text
	    else 'ANAMNESE_NAO_REALIZADA'
	end as STATUS_ANAMNESE_EVOLUCAO
   FROM agh.agh_atendimentos atd
     LEFT JOIN agh.agh_caract_unid_funcionais cuf ON cuf.unf_seq = atd.unf_seq AND cuf.caracteristica::text = 'Pme Informatizada'::text
     LEFT JOIN agh.agh_unidades_funcionais unf ON atd.unf_seq = unf.seq
     LEFT JOIN agh.rap_servidores raps ON raps.matricula = atd.ser_matricula AND raps.vin_codigo = atd.ser_vin_codigo
     LEFT JOIN agh.rap_pessoas_fisicas rapf ON raps.pes_codigo = rapf.codigo
     LEFT JOIN agh.aip_pacientes pac ON pac.codigo = atd.pac_codigo;

ALTER TABLE agh.v_mpm_lista_pac_internados
  OWNER TO postgres;
GRANT ALL ON TABLE agh.v_mpm_lista_pac_internados TO postgres;
GRANT ALL ON TABLE agh.v_mpm_lista_pac_internados TO acesso_completo;
GRANT SELECT ON TABLE agh.v_mpm_lista_pac_internados TO acesso_leitura;

--17/08/2016 #83265 Alterara Paramêtro PME_LISTA_PACIENTES_USAR_BUSCA_OTIMIZADA

update agh.agh_parametros set vlr_texto = 'S' where nome = 'PME_LISTA_PACIENTES_USAR_BUSCA_OTIMIZADA';

--17/08/2016 #83255 Ajuste de View Internação 

DROP VIEW agh.v_ain_internacao;

ALTER TABLE agh.ain_observacoes_censo ALTER COLUMN descricao TYPE text;

CREATE OR REPLACE VIEW agh.v_ain_internacao AS 
 SELECT inte.seq AS nro_internacao,
    inte.pac_codigo AS registro,
    qua.nro_reg_conselho AS crm,
    esp.nome_especialidade AS especialidade,
        CASE
            WHEN inte.unf_seq IS NOT NULL THEN inte.unf_seq
            ELSE unf2.seq
        END AS unidade_funcional,
    inte.iph_seq AS codigo_procedimento,
    lei.leito,
    lei.qrt_numero,
    cid.codigo AS cid_primario,
    date(inte.dthr_internacao) AS data,
    to_char(inte.dthr_internacao, 'HH:MI:SS'::text) AS hora,
    obs.descricao AS "observação",
    res.nome AS nome_responsavel,
    res.cpf AS documento_responsavel,
    res.ddd_fone::bigint | res.fone AS telefone,
    acp.nome AS acompanhante,
    oev.descricao AS origem,
    cids.codigo AS cid_secundario,
    inte.oev_seq,
    serl.usuario AS login,
    inte.dt_prev_alta,
    inte.doc_obito,
    inte.ind_paciente_internado,
    iph.cod_tabela AS procedimento_unificado
   FROM agh.ain_internacoes inte
     JOIN agh.rap_servidores ser ON inte.ser_matricula_professor = ser.matricula AND inte.ser_vin_codigo_professor = ser.vin_codigo
     JOIN agh.rap_pessoas_fisicas pes ON pes.codigo = ser.pes_codigo
     LEFT JOIN agh.rap_qualificacoes qua ON pes.codigo = qua.pes_codigo
     JOIN agh.agh_especialidades esp ON esp.seq = inte.esp_seq
     LEFT JOIN agh.agh_unidades_funcionais unf ON inte.unf_seq = unf.seq
     LEFT JOIN agh.ain_leitos lei ON inte.lto_lto_id::text = lei.lto_id::text
     LEFT JOIN agh.agh_unidades_funcionais unf2 ON lei.unf_seq = unf2.seq
     LEFT JOIN agh.ain_cids_internacao int_cid ON inte.seq = int_cid.int_seq AND int_cid.ind_prioridade_cid::text = 'P'::text
     LEFT JOIN agh.ain_responsaveis_paciente res ON inte.seq = res.int_seq AND res.tipo_responsabilidade::text = 'C'::text
     LEFT JOIN agh.ain_observacoes_censo obs ON inte.seq = obs.seq
     LEFT JOIN agh.ain_acompanhantes_internacao acp ON inte.seq = acp.int_seq
     JOIN agh.agh_origem_eventos oev ON inte.oev_seq = oev.seq
     LEFT JOIN agh.ain_cids_internacao int_cids ON inte.seq = int_cids.int_seq AND int_cids.ind_prioridade_cid::text = 'S'::text
     JOIN agh.rap_servidores serl ON inte.ser_matricula_digita = serl.matricula AND inte.ser_vin_codigo_digita = serl.vin_codigo
     JOIN agh.fat_itens_proced_hospitalar iph ON inte.iph_seq = iph.seq
     JOIN agh.agh_cids cid ON int_cid.cid_seq = cid.seq
     LEFT JOIN agh.agh_cids cids ON int_cids.cid_seq = cids.seq;

ALTER TABLE agh.v_ain_internacao
  OWNER TO postgres;
GRANT ALL ON TABLE agh.v_ain_internacao TO postgres;
GRANT ALL ON TABLE agh.v_ain_internacao TO acesso_completo;
GRANT SELECT ON TABLE agh.v_ain_internacao TO acesso_leitura;


--18/08/2016 #83376 Insere Paramêtro P_AGHU_NOME_ARQUIVO_FINANCIAMENTO

INSERT INTO AGH.AGH_PARAMETROS (ALTERADO_EM, ALTERADO_POR, CRIADO_EM, CRIADO_POR, DESCRICAO, MANTEM_HISTORICO, NOME, SIS_SIGLA, TIPO_DADO, VERSION, VLR_TEXTO, VLR_TEXTO_PADRAO, SEQ) 
	VALUES (now(), 'AGHU', now(), 'AGHU', 'Parâmetro que indica o nome da tabela do sigtap, referente ao tb_financiamento', 'S', 'P_AGHU_NOME_ARQUIVO_FINANCIAMENTO', 'AGH', 'N', 0, 'tb_financiamento.txt', 'tb_financiamento.txt', nextval('agh.agh_psi_sq1'));

-- 19/08/2016 #83461 Modificação da descrição do parametro, por Rafael Teixeira. 

update agh.agh_parametros
set descricao = descricao || '. PARA COBRAR A OBRIGATORIEDADE DO CAMPO N DA AUTORIZAÇÃO É NECESSÁRIO COLOCAR O VALOR DO TEXTO PARA ''S'' E ''N'' PARA NÃO TER QUE VALIDAR.'
,vlr_texto = 'N'
where seq = 7082; 	

-- 23/08/2016 #80556 Modificação no tamanho do campo origem internação;

DROP VIEW agh.v_ain_internacao;
DROP VIEW agh.v_ain_internacao_paciente;
			--altera campo
ALTER TABLE agh.agh_origem_eventos  ALTER COLUMN descricao TYPE CHARACTER VARYING(80);
			--cria novamente as visões vinculadas
CREATE OR REPLACE VIEW agh.v_ain_internacao AS 
 SELECT inte.seq AS nro_internacao,
    inte.pac_codigo AS registro,
    qua.nro_reg_conselho AS crm,
    esp.nome_especialidade AS especialidade,
        CASE
            WHEN inte.unf_seq IS NOT NULL THEN inte.unf_seq
            ELSE unf2.seq
        END AS unidade_funcional,
    inte.iph_seq AS codigo_procedimento,
    lei.leito,
    lei.qrt_numero,
    cid.codigo AS cid_primario,
    date(inte.dthr_internacao) AS data,
    to_char(inte.dthr_internacao, 'HH:MI:SS'::text) AS hora,
    obs.descricao AS "observação",
    res.nome AS nome_responsavel,
    res.cpf AS documento_responsavel,
    res.ddd_fone::bigint | res.fone AS telefone,
    acp.nome AS acompanhante,
    oev.descricao AS origem,
    cids.codigo AS cid_secundario,
    inte.oev_seq,
    serl.usuario AS login,
    inte.dt_prev_alta,
    inte.doc_obito,
    inte.ind_paciente_internado,
    iph.cod_tabela AS procedimento_unificado
   FROM agh.ain_internacoes inte
     JOIN agh.rap_servidores ser ON inte.ser_matricula_professor = ser.matricula AND inte.ser_vin_codigo_professor = ser.vin_codigo
     JOIN agh.rap_pessoas_fisicas pes ON pes.codigo = ser.pes_codigo
     LEFT JOIN agh.rap_qualificacoes qua ON pes.codigo = qua.pes_codigo
     JOIN agh.agh_especialidades esp ON esp.seq = inte.esp_seq
     LEFT JOIN agh.agh_unidades_funcionais unf ON inte.unf_seq = unf.seq
     LEFT JOIN agh.ain_leitos lei ON inte.lto_lto_id::text = lei.lto_id::text
     LEFT JOIN agh.agh_unidades_funcionais unf2 ON lei.unf_seq = unf2.seq
     LEFT JOIN agh.ain_cids_internacao int_cid ON inte.seq = int_cid.int_seq AND int_cid.ind_prioridade_cid::text = 'P'::text
     LEFT JOIN agh.ain_responsaveis_paciente res ON inte.seq = res.int_seq AND res.tipo_responsabilidade::text = 'C'::text
     LEFT JOIN agh.ain_observacoes_censo obs ON inte.seq = obs.seq
     LEFT JOIN agh.ain_acompanhantes_internacao acp ON inte.seq = acp.int_seq
     JOIN agh.agh_origem_eventos oev ON inte.oev_seq = oev.seq
     LEFT JOIN agh.ain_cids_internacao int_cids ON inte.seq = int_cids.int_seq AND int_cids.ind_prioridade_cid::text = 'S'::text
     JOIN agh.rap_servidores serl ON inte.ser_matricula_digita = serl.matricula AND inte.ser_vin_codigo_digita = serl.vin_codigo
     JOIN agh.fat_itens_proced_hospitalar iph ON inte.iph_seq = iph.seq
     JOIN agh.agh_cids cid ON int_cid.cid_seq = cid.seq
     LEFT JOIN agh.agh_cids cids ON int_cids.cid_seq = cids.seq;

ALTER TABLE agh.v_ain_internacao
  OWNER TO postgres;
GRANT ALL ON TABLE agh.v_ain_internacao TO postgres;
GRANT ALL ON TABLE agh.v_ain_internacao TO acesso_completo;
GRANT SELECT ON TABLE agh.v_ain_internacao TO acesso_leitura;

CREATE OR REPLACE VIEW agh.v_ain_internacao_paciente AS 
 SELECT inte.seq,
    inte.pac_codigo AS registro,
    pac.nome,
    pac.prontuario,
    pac.nro_cartao_saude AS cartao_sus,
    nac.descricao AS nacionalidade,
    pac.naturalidade,
    pac.grau_instrucao AS cod_escolaridade,
    pac.cor AS cod_cor,
    pac.rg,
    pac.cpf,
    pac.numero_pis,
    pac.reg_nascimento,
    pac.dt_nascimento,
    pac.nome_mae,
    pac.nome_pai,
    pac.sexo,
    pac.estado_civil,
    ende.logradouro AS "endereço",
    ende.nro_logradouro AS numero,
    ende.compl_logradouro AS complemento,
    ende.bairro,
    cid.uf_sigla AS estado,
    cid.nome AS cidade,
    cid.cep,
    endec.logradouro,
    pac.ddd_fone_residencial,
    pac.fone_residencial AS telefone,
    endec.nro_logradouro,
    endec.compl_logradouro AS complementoc,
    cidc.uf_sigla AS estadoc,
    cidc.nome AS cidadec,
    cidc.cep AS cepc,
    pac.ddd_fone_recado,
    pac.fone_recado,
    frh.fator_rh,
    peso.peso,
    alt.altura
   FROM agh.ain_internacoes inte
     JOIN agh.aip_pacientes pac ON inte.pac_codigo = pac.codigo
     JOIN agh.aip_nacionalidades nac ON pac.nac_codigo = nac.codigo
     JOIN agh.aip_enderecos_pacientes ende ON pac.codigo = ende.pac_codigo AND ende.tipo_endereco::text = 'R'::text
     LEFT JOIN agh.aip_cidades cid ON cid.codigo = ende.cdd_codigo
     LEFT JOIN agh.aip_enderecos_pacientes endec ON pac.codigo = endec.pac_codigo AND endec.tipo_endereco::text <> 'R'::text
     LEFT JOIN agh.aip_cidades cidc ON cidc.codigo = endec.cdd_codigo
     LEFT JOIN agh.aip_paciente_dado_clinicos frh ON pac.codigo = frh.pac_codigo
     LEFT JOIN agh.aip_peso_pacientes peso ON pac.codigo = peso.pac_codigo
     LEFT JOIN agh.aip_altura_pacientes alt ON pac.codigo = alt.pac_codigo;

ALTER TABLE agh.v_ain_internacao_paciente
  OWNER TO postgres;
GRANT ALL ON TABLE agh.v_ain_internacao_paciente TO postgres;
GRANT ALL ON TABLE agh.v_ain_internacao_paciente TO acesso_completo;
GRANT SELECT ON TABLE agh.v_ain_internacao_paciente TO acesso_leitura;

--24/08/2016 #83713 Alterado tamanho da coluna do cuidado de sumarios
ALTER TABLE agh.epe_item_cuidado_sumarios ALTER COLUMN descricao TYPE CHARACTER VARYING(240);	


--26/08/2016 #83640 Inserir permissões para o perfil MULT01

INSERT INTO AGH.CSE_PERFIS VALUES('MULT01','Acesso às telas básicas de OPS04 e OPS14 com atendimento Ambulatório',null,'M',null,'S','N','N','N','N',1,'N',null,'Perfil Multiprofissional com atendimento ambulatórial',0); 
INSERT INTO AGH.CSE_PERFIL_PROCESSOS VALUES((select seq from agh.cse_processos where nome = 'EVOLUCAO'),'MULT01','S','S','S','A',now(),9999999,955,'N','N',0); 
INSERT INTO AGH.CSE_PERFIL_PROCESSOS VALUES((select seq from agh.cse_processos where nome = 'ANAMNESE'),'MULT01','S','S','S','A',now(),9999999,955,'N','N',0); 
INSERT INTO AGH.CSE_PERFIL_PROCESSOS VALUES((select seq from agh.cse_processos where nome = 'PROCEDIMENTOS'),'MULT01','S','S','S','A',now(),9999999,955,'N','N',0); 
INSERT INTO AGH.CSE_PERFIL_PROCESSOS VALUES((select seq from agh.cse_processos where nome = 'ATESTADO'),'MULT01','S','S','S','A',now(),9999999,955,'N','N',0); 
INSERT INTO AGH.CSE_PERFIL_PROCESSOS VALUES((select seq from agh.cse_processos where nome = 'RECEITUARIO'),'MULT01','S','S','S','A',now(),9999999,955,'N','N',0); 
INSERT INTO AGH.CSE_CATEGORIA_PERFIS VALUES('MULT01',(select seq from agh.cse_categoria_profissionais where nome = 'Outro Profissional de Saúde'),'A',now(),9999999,955,0);
INSERT INTO AGH.CSE_CATEGORIA_PERFIS VALUES('MULT01',(select seq from agh.cse_categoria_profissionais where nome = 'Nutricionista'),'A',now(),9999999,955,0) ;


--26/08/2016 #83845 Inserindo parametros P_AGHU_NOME_ARQUIVO_REGISTRO e P_AGHU_NOME_ARQUIVO_PROCEDIMENTO_REGISTRO
INSERT INTO AGH.AGH_PARAMETROS 
(
	SEQ,
	SIS_SIGLA,
	NOME,
	DESCRICAO,	
	MANTEM_HISTORICO,
	CRIADO_EM,
	CRIADO_POR,	
	ALTERADO_EM,
	ALTERADO_POR,
	TIPO_DADO,
	VERSION,
	VLR_TEXTO,
	VLR_TEXTO_PADRAO
)
SELECT

	nextval('agh.agh_psi_sq1') AS SEQ,
	'AGH' AS SIS_SIGLA,
	'P_AGHU_NOME_ARQUIVO_REGISTRO' AS NOME,
	'Parâmetro que indica o nome da tabela do sigtap, referente ao tb_registro' AS DESCRICAO,	
	'S' AS MANTEM_HISTORICO,
	NOW() AS CRIADO_EM,
	'AGHU' AS CRIADO_POR,	
	NULL AS ALTERADO_EM,
	NULL AS ALTERADO_POR,
	'N' AS TIPO_DADO,
	0 AS VERSION,
	'tb_registro.txt' AS VLR_TEXTO,
	'tb_registro.txt' AS VLR_TEXTO_PADRAO
	
WHERE
	NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_AGHU_NOME_ARQUIVO_REGISTRO' AND sis_sigla = 'AGH');

INSERT INTO AGH.AGH_PARAMETROS 
(
	SEQ,
	SIS_SIGLA,
	NOME,
	DESCRICAO,	
	MANTEM_HISTORICO,
	CRIADO_EM,
	CRIADO_POR,	
	ALTERADO_EM,
	ALTERADO_POR,
	TIPO_DADO,
	VERSION,
	VLR_TEXTO,
	VLR_TEXTO_PADRAO
)
SELECT
	nextval('agh.agh_psi_sq1') AS SEQ,
	'AGH' AS SIS_SIGLA,
	'P_AGHU_NOME_ARQUIVO_PROCEDIMENTO_REGISTRO' AS NOME,
	'Parâmetro que indica o nome da tabela do sigtap, referente ao tb_procedimento_registro' AS DESCRICAO,
	'S' AS MANTEM_HISTORICO,
	NOW() AS CRIADO_EM,
	'AGHU' AS CRIADO_POR,
	NULL AS ALTERADO_EM,
	NULL AS ALTERADO_POR,	
	'N' AS TIPO_DADO,
	0 AS VERSION,
	'rl_procedimento_registro.txt' AS VLR_TEXTO,
	'rl_procedimento_registro.txt' AS VLR_TEXTO_PADRAO
WHERE
	NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_AGHU_NOME_ARQUIVO_PROCEDIMENTO_REGISTRO' AND sis_sigla = 'AGH');		
	

