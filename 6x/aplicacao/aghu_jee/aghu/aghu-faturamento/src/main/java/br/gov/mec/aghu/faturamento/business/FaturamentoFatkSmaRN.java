package br.gov.mec.aghu.faturamento.business;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.Stateless;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AelSismamaCitoCad;
import br.gov.mec.aghu.model.AelSismamaCitoRes;
import br.gov.mec.aghu.model.AelSismamaHistoCad;
import br.gov.mec.aghu.model.AelSismamaHistoRes;
import br.gov.mec.aghu.model.AelSismamaMamoCad;
import br.gov.mec.aghu.model.AelSismamaMamoRes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AipOrgaosEmissores;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipPacientesDadosCns;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatEspelhoSismama;
import br.gov.mec.aghu.model.FatItemEspelhoSismama;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatLogError;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * <p>
 * TODO Implementar metodos <br/>
 * Linhas: 2573 <br/>
 * Cursores: 0 <br/>
 * Forks de transacao: 1 <br/>
 * Consultas: 30 tabelas <br/>
 * Alteracoes: 48 tabelas <br/>
 * Metodos: 6 <br/>
 * Metodos externos: 8 <br/>
 * </p>
 * <p>
 * ORADB: <code>FATK_SMA_RN</code>
 * </p>
 * @author gandriotti
 *
 */
@Stateless
@SuppressWarnings("PMD.ExcessiveClassLength")
public class FaturamentoFatkSmaRN extends BaseBusiness implements Serializable {

private static final Log LOG = LogFactory.getLog(FaturamentoFatkSmaRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1056616311515438161L;
	
	/**
	 * <p>
	 * TODO <br/>
	 * Assinatura: ok<br/>
	 * Referencias externas: <b>NOK</b><br/>
	 * Tabelas: ok<br/>
	 * Codigos de erro: ok<br/>
	 * Implementacao: <b>NOK</b><br/>
	 * Linhas: 2198 <br/>
	 * Cursores: 0 <br/>
	 * Forks de transacao: 1 <br/>
	 * Consultas: 23 tabelas <br/>
	 * Alteracoes: 32 tabelas <br/>
	 * Metodos externos: 8 <br/>
	 * </p>
	 * <p>
	 * ORADB: <code>RN_SMAP_ATU_GERA_ESP</code>
	 * </p>
	 * <p>
	 * <b>INSERT:</b> {@link FatLogError} <br/>
	 * <b>INSERT:</b> {@link FatEspelhoSismama} <br/>
	 * <b>INSERT:</b> {@link FatItemEspelhoSismama} <br/>
	 * </p>
	 * <p>
	 * <b>UPDATE:</b> {@link AelSismamaHistoCad} <br/>
	 * <b>UPDATE:</b> {@link AelSismamaMamoCad} <br/>
	 * </p>
	 * <p>
	 * <b>DELETE:</b> {@link FatEspelhoSismama} <br/>
	 * <b>DELETE:</b> {@link FatItemEspelhoSismama} <br/>
	 * </p>
	 * @param pCpeDtHrInicio
	 * @param pCpeMes
	 * @param pCpeAno
	 * @param pCpeDtFim
	 * @param isPrevia
	 * @return
	 *  
	 * @see FatProcedAmbRealizado
	 * @see FatConvenioSaude
	 * @see FatItensProcedHospitalar
	 * @see FatVlrItemProcedHospComps
	 * @see FatCompetencia
	 * @see AipOrgaosEmissores
	 * @see AipPacientesDadosCns
	 * @see AipPacientes
	 * @see ScoFornecedor
	 * @see AelSolicitacaoExames
	 * @see AelExtratoItemSolicitacao
	 * @see RapPessoasFisicas
	 * @see RapServidores
	 * @see AelItemSolicitacaoExames
	 * @see AelResultadoExame
	 * @see AelSismamaHistoRes
	 * @see AelSismamaMamoRes
	 * @see AelSismamaCitoRes
	 * @see FatEspelhoSismama
	 * @see AelSismamaHistoCad
	 * @see AelSismamaMamoCad
	 * @see AelSismamaCitoCad
	 * @see FatLogError
	 * @see FatItemEspelhoSismama
	 * @see FaturamentoExceptionCode#FAT_00495
	 * @see FaturamentoFatkCpeRN#rnCpecAtuEncComp(String, Date, Integer, Integer, Date, Integer, Integer)
	 * 
	 * @see FaturamentoFatkEpsRN#rnEpspAtuSituacao(boolean, String, Integer)
	 * @see FaturamentoFatkPmrRN#rnPmrpAtuRegras(Integer, Short, Integer, Short, Integer, Date, boolean, String)
	 * @see #rnSmapInsereS02(Integer, String, Integer, Integer)
	 * @see #rnSmapInsereS04(Integer, String, Integer, Integer)
	 * @see #rnSmapInsereS06(Integer, String, Integer, Integer)
	 * @see #rnSmapAtuSituacao(boolean, String, Integer)
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public boolean rnSmapAtuGeraEsp(Date pCpeDtHrInicio, Integer pCpeMes, Integer pCpeAno, Date pCpeDtFim, boolean isPrevia) throws ApplicationBusinessException {
		throw new NotImplementedException("Este método ainda não foi implementado"); // TODO
//		-- 
//		-- Sub-Program Units
//		-- 
//		PROCEDURE RN_SMAP_ATU_GERA_ESP
//		 (P_CPE_DT_HR_INICIO IN DATE
//		 ,P_CPE_MES IN NUMBER
//		 ,P_CPE_ANO IN NUMBER
//		 ,P_CPE_DT_FIM IN DATE
//		 ,P_PREVIA IN VARCHAR2
//		 )
//		IS
//		CURSOR c_proc_amb
//			(c_dt_fim IN DATE) 
//		IS
//		  SELECT 
//		      pmr.SEQ
//		  	, pmr.DTHR_REALIZADO
//		    , pmr.LOCAL_COBRANCA
//		    , pmr.QUANTIDADE
//		    , pmr.VALOR
//		    , pmr.CPE_MES
//		    , pmr.CPE_ANO
//		    , pmr.CPE_DT_HR_INICIO
//		    , pmr.CPE_MODULO
//		    , pmr.ISE_SEQP
//		    , pmr.ISE_SOE_SEQ
//		    , pmr.PRH_CON_NUMERO
//		    , pmr.PRH_PHI_SEQ
//		    , pmr.PHI_SEQ
//		    , pmr.UNF_SEQ
//		    , pmr.ESP_SEQ
//		    , pmr.PAC_CODIGO
//		    , pmr.CSP_CNV_CODIGO
//		    , pmr.CSP_SEQ
//		    , pmr.IND_ORIGEM
//			, pmr.criado_em
//			, pmr.ser_matricula_resp
//			, pmr.ser_vin_codigo_resp
//		  FROM 
//		    fat_proced_amb_realizados pmr
//		  WHERE
//		        pmr.ise_soe_seq IS NOT NULL
//			AND pmr.ise_seqp        IS NOT NULL
//			AND pmr.dthr_realizado <=  c_dt_fim
//		--    AND pmr.ind_situacao     = 'C'
//		    AND pmr.ind_situacao in    ( 'A','C')			
//		    AND pmr.cpe_modulo       = 'AMB'
//		    AND pmr.cpe_dt_hr_inicio = p_cpe_dt_hr_inicio
//		    AND pmr.cpe_mes          = p_cpe_mes
//		    AND pmr.cpe_ano          = p_cpe_ano
//		    AND pmr.csp_cnv_codigo  IN 
//		        (SELECT cnv.codigo
//		           FROM fat_convenios_saude cnv
//		          WHERE cnv.grupo_convenio = 'S')
//		    AND fatk_sma_rn.rn_smac_ver_sismama(pmr.ise_soe_seq, pmr.ise_seqp)  
//		         IN (  1 -- Histo 
//					 ,2 -- Mamografia
//					 ,3 --  Cito
//				     ) 
//					 ;
//		CURSOR c_iph_tabela 
//		    (p_iph_pho_seq IN NUMBER,
//		     p_iph_seq     IN NUMBER) 
//		IS
//		  SELECT 
//		       iph.cod_tabela
//		     , iph.ind_proc_principal_apac
//		     , FCC_SEQ
//		     , FCF_SEQ
//		  FROM 
//		     fat_itens_proced_hospitalar iph
//		  WHERE 
//		         iph.pho_seq = p_iph_pho_seq
//		     AND iph.seq     = p_iph_seq
//		     AND iph.ind_situacao = 'A';
//		--
//		CURSOR c_ind_consulta 
//			(p_pho_seq IN NUMBER, 
//			 p_seq     IN NUMBER) 
//		IS
//		  SELECT 
//		     NVL(iph.ind_consulta, 'N')
//		  FROM 
//		     fat_itens_proced_hospitalar iph
//		  WHERE 
//		         iph.pho_seq = p_pho_seq
//		     AND iph.seq     = p_seq;
//		--
//		CURSOR c_valores 
//			(p_iph_pho_seq IN NUMBER,
//		     p_iph_seq     IN NUMBER,
//		     p_cpe_competencia IN DATE ) 
//		IS
//		  SELECT 
//		       ipc.vlr_serv_hospitalar
//		     , ipc.vlr_serv_profissional
//			 , NVL(ipc.vlr_serv_profis_ambulatorio,0) vlr_serv_profis_ambulatorio
//		     , ipc.vlr_sadt
//		     , ipc.vlr_procedimento
//		     , ipc.vlr_anestesista
//		  FROM 
//		     fat_vlr_item_proced_hosp_comps ipc
//		  WHERE 
//		         ipc.iph_seq     = p_iph_seq
//		     AND ipc.iph_pho_seq = p_iph_pho_seq
//		     AND ipc.dt_inicio_competencia <= p_cpe_competencia
//		     AND (ipc.dt_fim_competencia >= p_cpe_competencia OR
//		          ipc.dt_fim_competencia IS NULL);
//		--
//		CURSOR c_dt_competencia 
//		IS
//		  SELECT 
//		     dt_hr_fim
//		  FROM 
//		     fat_competencias
//		  WHERE 
//		         MODULO       =  'MAMA'  -- MARINA 06/10/2009      --'SIS' ----- < VER VER VER
//		     AND MES          = p_cpe_mes
//		     AND ANO          = p_cpe_ano
//		     AND DT_HR_INICIO = p_cpe_dt_hr_inicio;
//		--
//		CURSOR c_dados_paciente 
//			(c_pac_codigo	 aip_pacientes.codigo%type,
//			 c_dthr_realizado	 fat_proced_amb_realizados.dthr_realizado%type) 
//		IS
//		  SELECT
//		    decode(PAC.SEXO,'M','1','F','2','0') sexo,
//		    pac.prontuario,
//			pac.nome,
//			pac.nome_mae,
//			pac.rg,
//			substr(
//			 replace(
//			  DECODE(PAC.RG,NULL,NULL,
//			   DECODE(pac.orgao_emis_rg,NULL,
//			    NVL(SUBSTR(OED.DESCRICAO,1,3),'SSPPC'),
//			    pac.orgao_emis_rg)),'/',null),1,5) OEMRG,
//			--pac.orgao_emis_rg,
//			pac.cpf,
//			pac.dt_nascimento,
//			TRUNC(MONTHS_BETWEEN(c_dthr_realizado,pac.dt_nascimento)/12) idade,
//			SUBSTR(AIPC_GET_LOGRAD_PAC(pac.codigo),1,35) logradouro,
//			AIPC_GET_NRO_END_PAC(pac.codigo) nro,
//			AIPC_GET_compl_END(pac.codigo) compl,
//			SUBSTR(aipc_get_bairro_pac(pac.codigo),1,20) bairro,
//			to_number(AIPC_GET_COD_IBGE(pac.codigo)||AGHC_MODULO10(AIPC_GET_COD_IBGE(pac.codigo))) ibge,
//			AIPC_GET_UF_COD(AIPC_GET_uf_pac(pac.codigo)) uf,
//			-- AIPC_GET_uf_cod (pac.codigo) uf,
//			--DECODE(pac.cor,'B',1,'A',2,'I',3,'M',4,'P',5,0) raca,
//			DECODE(cor,'B',1, 'P', 2, 'M', 3, 'A', 4, 'I', 5, 'O', 99,99) raca,
//			SUBSTR(AIPC_GET_FONE_PAC(pac.codigo),1,11) fone,
//			aipc_get_cep_pac(pac.codigo) cep,
//			DECODE(pac.grau_instrucao,1,3,2,4,3,5,4,1,5,0,6,2,7,3,8,4,0)escolaridade,
//			DECODE(PAC.RG,NULL,NULL,
//			       NVL(AIPC_GET_UF_COD(cns.UF_SIGLA_EMITIU_DOCTO),
//				   AIPC_GET_UF_COD((AIPC_GET_uf_pac(pac.codigo))))) ufrg,
//		    pac.nro_cartao_saude,
//		    aipc_get_uf_cod('RS') us_uf
//		  FROM 	
//		    AIP_ORGAOS_EMISSORES OED,
//			AIP_PACIENTES_DADOS_CNS cns,
//			aip_pacientes pac
//		  WHERE
//		   	    pac.codigo = c_pac_codigo
//			AND cns.pac_codigo(+) = pac.codigo
//			AND	OED.CODIGO(+) = CNS.OED_CODIGO;
//		--
//		reg_pac	   		 c_dados_paciente%rowtype;
//		reg_proc_amb     c_proc_amb%rowtype;
//		reg_valor        c_valores%rowtype;
//		--
//		CURSOR c_busca_cgc 
//		IS
//		  SELECT 
//		    cgc,
//		    razao_social,
//		    cdd_codigo 
//		  FROM 
//		    sco_fornecedores 
//		  WHERE 
//		     numero = 1;
//		--
//		CURSOR c_busca_dt_solic 
//			(c_ise_soe_seq   fat_proced_amb_realizados.ise_soe_seq%type) 
//		IS
//		  SELECT  
//		    TRUNC(soe.criado_em)
//		  FROM     
//		    ael_solicitacao_exames  soe
//		  WHERE  
//		    soe.seq = c_ise_soe_seq;
//		--
//		CURSOR c_busca_dt_recebe 
//			(c_ise_soe_seq   fat_proced_amb_realizados.ise_soe_seq%type,
//			 c_ise_seqp      fat_proced_amb_realizados.ise_soe_seq%type) 
//		IS
//		  SELECT  
//		    TRUNC(eis.dthr_evento)
//		  FROM    
//		    ael_extrato_item_solics eis
//		  WHERE 
//		        eis.ise_soe_seq = c_ise_soe_seq
//			AND eis.ise_seqp = c_ise_seqp
//			AND SUBSTR(eis.sit_codigo,1,2) = 'AE'
//		  ORDER BY 
//		    eis.seqp;
//		--
//		CURSOR c_busca_resp 
//			(c_ise_soe_seq   fat_proced_amb_realizados.ise_soe_seq%type,
//			 c_ise_seqp      fat_proced_amb_realizados.ise_soe_seq%type) 
//		IS
//		  SELECT 	
//		    lpad(pes.cpf,11,'0') cpf
//		  FROM   	
//		    rap_pessoas_fisicas pes,
//			rap_servidores ser,
//			ael_item_solicitacao_exames ise
//		  WHERE
//		        ise.soe_seq = c_ise_soe_seq
//			AND ise.seqp = c_ise_seqp
//			AND ser.matricula = ise.SER_MATRICULA_EH_RESPONSABILID
//			AND	ser.vin_codigo = ise.SER_VIN_CODIGO_EH_RESPONSABILI
//			AND pes.codigo = ser.pes_codigo;
//		--
//		CURSOR c_busca_nro_exame 
//			(c_ise_soe_seq   fat_proced_amb_realizados.ise_soe_seq%type,
//			 c_ise_seqp      fat_proced_amb_realizados.ise_soe_seq%type,
//			 c_ano			 NUMBER) 
//		IS
//		  SELECT  
//		     1212||LPAD(NVL(SUBSTR(TO_CHAR(c_ano),3,2),'0'), 2, '0')
//			 ||LPAD(NVL(TO_CHAR(ree.valor), '0'),  6, '0') -- Milena alterou para 6 05/12/05
//		  FROM    
//		     ael_resultados_exames ree,
//			 ael_item_solicitacao_exames ise
//		  WHERE	  
//		         ise.soe_seq = c_ise_soe_seq
//		     AND ise.seqp = c_ise_seqp
//		     AND ree.ise_soe_seq = ise.soe_seq
//		     AND ree.ise_seqp = ise.seqp
//		     AND ree.pcl_vel_ema_exa_sigla = ise.ufe_ema_exa_sigla
//		     AND ree.pcl_vel_ema_man_seq = ise.ufe_ema_man_seq
//		     AND ree.pcl_seqp = DECODE(ree.pcl_vel_seqp,19,1,2)
//		     AND ree.ind_anulacao_laudo = 'N'
//		     AND REE.PCL_CAL_SEQ    = 873;
//		--
//		v_cpf	rap_pessoas_fisicas.cpf%type := NULL;
//		v_nro_exame    NUMBER(14) := 0;
//		v_dt_recebe	DATE	:= NULL;
//		v_dt_coleta		DATE	:= NULL;
//		v_ano_prev		DATE  := NULL;
//		v_cgc			 sco_fornecedores.cgc%type	:= NULL;
//		v_razao_social	 sco_fornecedores.razao_social%type := null;
//		v_cdd_codigo	 sco_fornecedores.cdd_codigo%type := null;
//		v_iph_pho_seq    fatk_sus_rn.t_number;
//		v_iph_qtd_item   fatk_sus_rn.t_number;
//		v_iph_seq        fatk_sus_rn.t_number;
//		v_iph_cod_tab    fat_itens_proced_hospitalar.cod_tabela%type;
//		-- 
//		V_IPH_FCC_SEQ    FAT_ITENS_PROCED_HOSPITALAR.FCC_SEQ%TYPE;
//		V_IPH_FCF_SEQ    FAT_ITENS_PROCED_HOSPITALAR.FCF_SEQ%TYPE;
//		--
//		v_cav_cod_sus    fat_convs_atividade_prof.codigo_sus%type;
//		v_ctc_tps_tipo   fat_conv_tipo_consultas.codigo_sus%type;
//		-- 
//		--tps_tipo
//		--v_tps_ind_emergencia aac_tipo_consultas.ind_emergencia%type;
//		-- 
//		v_tps_ind_emergencia VARCHAR2(1);  --aac_consultas.tps_tipotype;
//		v_gra_cod_sus    fat_grupos_atendimentos.codigo_sus%type;
//		v_cfe_cod_sus    fat_conv_faixa_etarias.codigo_sus%type;
//		v_cfe_cod_sus_item fat_conv_faixa_etarias.codigo_sus%type;
//		--
//		v_vlr_data	    DATE;
//		v_vlr_numero     NUMBER;
//		v_vlr_char       VARCHAR2(30);
//		v_cpf_prof       NUMBER(12);
//		v_msg            VARCHAR2(300);
//		--
//		v_idade          NUMBER(4);
//		v_qtd_itens      NUMBER(4);
//		v_indice         NUMBER(10);
//		v_consistente    VARCHAR2(1);
//		regras_ok        BOOLEAN;
//		erro             BOOLEAN;
//		v_ind_consulta   VARCHAR2(1);
//		v_competencia    NUMBER(6):=TO_NUMBER(TO_CHAR(p_cpe_ano)
//		        			||LPAD(TO_CHAR(p_cpe_mes),2,'0'));
//		v_data_previa    DATE      := NULL;
//		v_aux            VARCHAR2(1);
//		v_dt_fim_cpe     DATE;
//		--
//		v_cpe_dthr_fim 		fat_competencias.dt_hr_fim%type;
//		v_new_cpe_dthr_inicio		fat_competencias.dt_hr_inicio%type;
//		v_new_cpe_mes		fat_competencias.mes%type;
//		v_new_cpe_ano		fat_competencias.ano%type;
//		--
//		v_max_dthr_realiz	fat_competencias.dt_hr_fim%type;
//		v_sysdate			DATE;
//		v_cpe_competencia    DATE;
//		v_contador           NUMBER(6) := 0;
//		v_sqlerrm            VARCHAR2(1000);
//		v_princ_apac        fat_itens_proced_hospitalar.ind_proc_principal_apac%type;
//		v_cobra_siscolo 	VARCHAR2(1);
//		v_dum			DATE;
//		v_limite_normal		VARCHAR2(1);
//		v_mot_rej_aus		VARCHAR2(1);
//		v_mot_rej_dan		VARCHAR2(1);
//		v_mot_rej_alh		VARCHAR2(1);
//		v_mot_rej_out		VARCHAR2(1);
//		v_mot_rej_oesp		VARCHAR2(40);
//		v_mot_rej_aesp		VARCHAR2(40);
//		v_adeq_mat		VARCHAR2(1);
//		v_bem_out			VARCHAR2(1) := '0';
//		v_bem_outr		VARCHAR2(40) := NULL;
//		v_adq_out 		VARCHAR2(1) := '0';
//		v_adq_outr		VARCHAR2(40);
//		v_mic_out			VARCHAR2(1) := '0';
//		v_mic_outr		VARCHAR2(40);
//		v_neo_mali		VARCHAR2(1) := '0';
//		v_neo_mout		VARCHAR2(40);
//		v_obs_ger1		VARCHAR2(1) := '0';
//		v_obs_ger			VARCHAR2(40);
//		v_cns_medico		VARCHAR2(15);
//		-- 
//		v_sma_seq			fat_espelhos_sismama.seq%type;
//		v_iem_seq			FAT_ITENS_ESPELHO_SISMAMA.seq%type;
//		v_nro_exame_2		fat_espelhos_sismama.nro_exame%type;
//		v_cod_tabela		fat_espelhos_sismama.cod_tabela%type;
//		v_dt_liberacao		fat_espelhos_sismama.dt_liberacao%type;
//		v_pmr_seqe			fat_espelhos_sismama.pmr_seq%type;
//		v_unf_seq			fat_espelhos_sismama.unf_seq%type;
//		v_data_previa_2		fat_espelhos_sismama.data_previa%type;
//		v_cod_ans			conv.cod_ans%type;
//		-- 
//		v_tipo_proc			number(2);
//		v_sair_2			varchar2(1);
//		v_s02_seq			ael_sismama_histo_res.seq%type;
//		v_s04_seq			ael_sismama_mamo_res.seq%type;
//		v_s06_seq			ael_sismama_cito_res.seq%type;
//		v_codigo	        ael_sismama_histo_res.s01_codigo%type;
//		V_SER_VIN_CODIGO_RET NUMBER :=	NULL;
//		V_SER_MATR_RET		 NUMBER :=	NULL;
//		v_achou_cad_res		 varchar2(1);
//		v_resposta ael_sismama_mamo_res.resposta%type;
//		v_resposta_C_CLI_DIAG ael_sismama_mamo_res.resposta%type;
//		-- 
//		cursor cur_dado_histo
//			(c_ise_soe_seq		ael_sismama_histo_res.ise_soe_seq%type,
//			 c_ise_seqp			ael_sismama_histo_res.ise_seqp%type)
//		is
//		  select
//		    s02.seq s02_seq,
//		    null    s04_seq,
//		    null    s06_seq
//		  from
//		    ael_sismama_histo_res s02
//		  where
//		    s02.ise_soe_seq = c_ise_soe_seq and
//		    s02.ise_seqp    = c_ise_seqp;
//		-- 
//		cursor cur_dado_mamo
//			(c_ise_soe_seq		ael_sismama_histo_res.ise_soe_seq%type,
//			 c_ise_seqp			ael_sismama_histo_res.ise_seqp%type)
//		is
//		  select
//		    null    s02_seq,
//		    s04.seq s04_seq,
//		    null    s06_seq
//		  from
//		    ael_sismama_mamo_res s04
//		  where
//		    s04.ise_soe_seq = c_ise_soe_seq and
//		    s04.ise_seqp    = c_ise_seqp;    
//		--
//		cursor cur_dado_cito
//			(c_ise_soe_seq		ael_sismama_histo_res.ise_soe_seq%type,
//			 c_ise_seqp			ael_sismama_histo_res.ise_seqp%type)
//		is
//		  select
//		    null    s02_seq,
//		    null    s04_seq,
//		    s06.seq s06_seq
//		  from
//		    ael_sismama_cito_res s06
//		  where
//		    s06.ise_soe_seq = c_ise_soe_seq and
//		    s06.ise_seqp    = c_ise_seqp;    
//		-- 
//		cursor cur_conv
//		is
//		  select
//		    cod_ans
//		  from
//		    conv
//		  where 
//		    cod = 99;
//		--
//		cursor cur_sma
//			(c_cpe_dt_hr_inicio		fat_espelhos_sismama.cpe_dt_hr_inicio%type,
//			 c_cpe_mes				fat_espelhos_sismama.cpe_mes%type,
//			 c_cpe_ano				fat_espelhos_sismama.cpe_ano%type)
//		is
//		  select 
//		     sma.seq
//		  from
//		     fat_espelhos_sismama sma
//		  where sma.data_previa      IS NOT NULL
//		    and sma.cpe_dt_hr_inicio = c_cpe_dt_hr_inicio
//		    and sma.cpe_mes          = c_cpe_mes
//		    and sma.cpe_ano          = c_cpe_ano
//		    and sma.cpe_modulo       = 'MAMA'
//		for update of sma.seq
//			;
//		-- 
//		cursor cur_histo_cad
//		is
//		  select
//		     s01.codigo
//		  from
//		     ael_sismama_histo_cad s01
//		  where
//		     s01.tipo is not null and
//		     s01.tipo <> 'ID';
//		-- 
//		cursor cur_histo_res_det
//			(c_ise_soe_seq		ael_sismama_histo_res.ise_soe_seq%type,
//			 c_ise_seqp			ael_sismama_histo_res.ise_seqp%type,
//			 c_s01_codigo		ael_sismama_histo_res.s01_codigo%type)
//		is
//		  select
//		     s02.resposta
//		  from
//		     ael_sismama_histo_res s02
//		  where
//		      s02.ise_soe_seq     = c_ise_soe_seq and
//		      s02.ise_seqp        = c_ise_seqp and
//		      s02.s01_codigo||''  = c_s01_codigo
//			  FOR update of s02.resposta;       
//		-- 
//		cursor cur_mamo_cad
//		is
//		  select
//		     s03.codigo
//		  from
//		     ael_sismama_mamo_cad s03
//		  where
//		     s03.tipo is not null and
//		     s03.tipo <> 'ID';
//		-- 
//		cursor cur_mamo_res_det
//			(c_ise_soe_seq		ael_sismama_mamo_res.ise_soe_seq%type,
//			 c_ise_seqp			ael_sismama_mamo_res.ise_seqp%type,
//			 c_s03_codigo		ael_sismama_mamo_res.s03_codigo%type)
//		is
//		  select
//		     s04.resposta
//		  from
//		     ael_sismama_mamo_res s04
//		  where
//		     s04.ise_soe_seq     = c_ise_soe_seq and
//		     s04.ise_seqp        = c_ise_seqp and
//		     s04.s03_codigo||''  = c_s03_codigo
//		FOR update of s04.resposta;      
//		-- 
//		cursor cur_cito_cad
//		is
//		  select
//		     s05.codigo
//		  from
//		     ael_sismama_cito_cad s05
//		  where
//		     s05.tipo is not null and
//		     s05.tipo <> 'ID';
//		-- 
//		cursor cur_cito_res_det
//			(c_ise_soe_seq		ael_sismama_cito_res.ise_soe_seq%type,
//			 c_ise_seqp			ael_sismama_cito_res.ise_seqp%type,
//			 c_s05_codigo		ael_sismama_cito_res.s05_codigo%type)
//		is
//		  select
//		     'S'
//		  from
//		     ael_sismama_cito_res s06
//		  where
//		     s06.ise_soe_seq     = c_ise_soe_seq and
//		     s06.ise_seqp        = c_ise_seqp and
//		     s06.s05_codigo||''  = c_s05_codigo;      
//		-- 
//		PROCEDURE SALVAR_PROCESS 
//		IS
//		BEGIN
//		    -- 
//		    COMMIT;
//		    -- 
//		END SALVAR_PROCESS;
//		--
//		BEGIN
//		--
//		v_sysdate := SYSDATE;
//		-- 
//		-- limpa log erros para o sismama
//		-- 
//		DELETE FAT_LOG_ERRORS
//		 WHERE MODULO = 'MAMA';
//		--
//		-- delete itens do espelho para sismama
//		-- 
//		begin
//		for r_sma in cur_sma
//			(p_cpe_dt_hr_inicio,
//			 p_cpe_mes,
//			 p_cpe_ano)
//		loop
//		   -- 
//		   dbms_output.put_line('deleting iem sma='||r_sma.seq);
//		   delete from fat_itens_espelho_sismama iem
//		    where iem.sma_seq = r_sma.seq;
//			   dbms_output.put_line('deleted iem sma='||sql%rowcount);
//		   -- 
//		   dbms_output.put_line('deleting sma seq='||r_sma.seq);   
//		   DELETE FROM FAT_ESPELHOS_SISMAMA where current of cur_sma;
//		   dbms_output.put_line('deleted sma='||sql%rowcount);    
//		   --
//		end loop;
//		exception
//		 when others then
//		        raise_application_error(-20000,'Erro FATK_SMA_RN.RN_SMAP_ATU_GERA_ESP '
//		                                       ||v_sqlerrm);
//		end;									  
//		-- 	 
//		-- deleta os espelhos do sismama
//		-- 
//		/*DELETE FROM FAT_ESPELHOS_SISMAMA
//		 WHERE data_previa      IS NOT NULL
//		   AND cpe_dt_hr_inicio = p_cpe_dt_hr_inicio
//		   AND cpe_mes          = p_cpe_mes
//		   AND cpe_ano          = p_cpe_ano
//		   AND cpe_modulo       = 'MAMA'; */
//		--
//		SALVAR_PROCESS;
//		--
//		IF p_previa = 'S' 
//		THEN
//		   -- 
//		   fatk_sma_rn.v_sma_encerramento := FALSE;
//		   -- 
//		   v_data_previa := TRUNC(SYSDATE);
//		   v_dt_fim_cpe := NVL(p_cpe_dt_fim,SYSDATE);
//		   v_max_dthr_realiz := NVL(p_cpe_dt_fim,SYSDATE);
//		   -- 
//		ELSE
//		   -- 
//		   fatk_sma_rn.v_sma_encerramento := TRUE;
//		   -- 
//		   OPEN c_dt_competencia;
//		   FETCH c_dt_competencia INTO v_dt_fim_cpe;
//		   CLOSE c_dt_competencia;
//		   -- 
//		   IF v_dt_fim_cpe IS NULL 
//		   THEN
//		      -- 
//		      -- vai atualizar competencia com dt fim
//		      -- e situacao = M
//		      -- 
//		      v_cpe_dthr_fim := NVL(p_cpe_dt_fim,SYSDATE);
//		      -- 
//		      IF fatk_cpe_rn.rn_cpec_atu_enc_comp (
//		                            'MAMA'
//		                             ,p_cpe_dt_hr_inicio
//		                             ,p_cpe_mes
//		                             ,p_cpe_ano
//		                             ,v_cpe_dthr_fim
//		                   	         ,v_new_cpe_dthr_inicio
//		    	           	         ,v_new_cpe_mes
//		    	           	         ,v_new_cpe_ano	) 
//		      THEN
//		         -- 
//		         v_dt_fim_cpe := v_cpe_dthr_fim;
//		         -- 
//		      ELSE
//		         -- 
//		         -- Erro ao encerrar competência e criar nova competência aberta
//		         -- 
//		         raise_application_error (-20000, 'FAT-00495 '||'RN_SMAP_ATU_GERA_ESP');
//		         -- 
//		      END IF;
//		      -- 
//		   END IF;
//		   -- 
//		   v_max_dthr_realiz := v_dt_fim_cpe;
//		   -- 
//		END IF;
//		        	 aghp_grava_mensagem(
//		             'Iniciando processamento de RN_SMAP_ATU_GERA_ESP em '||to_char(sysdate,'dd/mm/yy hh24:mi:ss')
//		              ,'FAT_NEY','N');
//		-- 
//		-- Obtem cpf do profissional responsável pela citologia
//		-- 
//		aghp_get_parametro('P_CPF_PROF_CITOP',
//		                   'RN_SMAP_ATU_GERA_ESP','N','N',v_vlr_data,
//		                   v_cpf_prof,v_vlr_char,v_msg);
//		--                    
//		IF v_msg IS NOT NULL OR v_cpf_prof IS NULL 
//		THEN --(37)
//		   -- 
//		   raise_application_error(-20000, NVL(v_msg,
//		                           'Erro ao buscar parâmetro P_CPF_PROF_CITOP')||
//		                           ' RN_SMAP_ATU_GERA_ESP');
//		   -- 
//		END IF;
//		-- 
//		-- obtem cns do profissional da citopatologia
//		-- 
//		aghp_get_parametro('P_CNS_PROF_CITOP',
//		                         'RN_SMAP_ATU_GERA_ESP','N','N',v_vlr_data,
//		                         v_vlr_numero,v_cns_medico,v_msg);
//		-- 
//		IF v_msg IS NOT NULL 
//		THEN --(37)
//		   -- 
//		   raise_application_error(-20000, NVL(v_msg,
//		                           'Erro ao buscar parâmetro P_CNS_PROF_CITOP')||
//		                           ' RN_SMAP_ATU_GERA_ESP');
//		   -- 
//		END IF;
//		--
//		OPEN c_proc_amb(v_max_dthr_realiz);
//		LOOP
//		   -- 
//		   BEGIN -- LMF: EXCEPTION DE ERRO NÃO PREVISTO
//		      -- 
//		      FETCH c_proc_amb INTO reg_proc_amb;
//		      -- 
//		      EXIT WHEN c_proc_amb%notfound;
//		      -- 
//		      v_consistente := 'S';
//		      -- 
//		      -- FAZER COMMIT A CADA 1000 REGISTROS  -- hco]
//		      -- 
//		      v_contador := v_contador + 1;
//		      -- 
//		      -- if p_previa = 'N' then      -- Não sei porque disto, por isto comentei (original já tinha isto)
//		      --    t_amb(v_contador) := reg_proc_amb.seq;
//		      -- end if;
//		      -- 
//		      if mod(v_contador, 1000) = 0 
//		      then
//		         -- 
//		         SALVAR_PROCESS;
//		         -- 
//		      end if;
//		      --
//		      v_indice := 1;
//		      v_ind_consulta := 'N';
//		      v_qtd_itens := fatk_sus_rn.rn_fatc_ver_itproc (
//		                 			 reg_proc_amb.phi_seq
//		                   			,reg_proc_amb.quantidade
//		                   			,reg_proc_amb.csp_cnv_codigo
//		                   			,reg_proc_amb.csp_seq
//		                   			,'A'
//		                   			,v_iph_pho_seq
//		                   			,v_iph_qtd_item
//		                   			,v_iph_seq);
//		      --              			
//		      loop
//		          -- 
//		          -- nao é uma prévia e houve erro anteriormente
//		          -- 
//		          exit when p_previa = 'N' and v_consistente = 'N';
//		          --
//		          if v_qtd_itens <= 0 
//		          then
//		             -- 
//		    	     -- nao encontrou item procedimento hospitalar
//		    	     -- 
//		             INSERT INTO FAT_LOG_ERRORS 
//		                             (MODULO, 
//		                              ERRO, 
//		                              PROGRAMA,
//		                              CRIADO_EM, 
//		                              CRIADO_POR,
//		                              CTH_SEQ, 
//		                              CAP_ATM_NUMERO, 
//		                              CAP_SEQP, 
//		                              COD_ITEM_SUS_1, 
//		                              COD_ITEM_SUS_2,
//		                              DATA_PREVIA, 
//		                              ICA_SEQP, 
//		                              ICH_SEQP,
//		                              IPH_PHO_SEQ,
//		                              IPH_PHO_SEQ_REALIZADO,
//		                              IPH_SEQ, 
//		                              IPH_SEQ_REALIZADO, 
//		                              PAC_CODIGO, 
//		                              PHI_SEQ, 
//		                              PHI_SEQ_REALIZADO,
//		                              PMR_SEQ, 
//		                              PRONTUARIO )
//		                             VALUES 
//		                             ('MAMA', 
//		                              '001 - NAO ENCONTROU ITEM PROCEDIMENTO HOSPITALAR.',
//		                    		  'RN_SMAP_ATU_GERA_ESP',
//		                              SYSDATE, 
//		                              USER, 
//		                              NULL, 
//		                              NULL, 
//		                              NULL, 
//		                              NULL, 
//		                              NULL, 
//		                              v_data_previa,
//		                              NULL, 
//		                              NULL, 
//		                              NULL, 
//		                              NULL, 
//		                              NULL, 
//		                              NULL,
//		                              reg_proc_amb.pac_codigo, 
//		                              reg_proc_amb.phi_seq, 
//		                              NULL,
//		                              reg_proc_amb.seq, 
//		                              NULL );
//		             --                   
//		             -- Inibido em jan/2008 - os procedimentos serão cobrados em BPA
//		             -- desenibido em fev/2008 - Milena
//		             --fatk_EPS_rn.RN_EPSP_ATU_SITUACAO (
//		             --     		p_previa, 'C', reg_proc_amb.seq );
//		             v_consistente := 'N';
//		             --
//		             exit when p_previa <> 'S';
//		             -- 
//		          end if;
//		          --
//		          if v_qtd_itens > 0 
//		          then
//		             -- 
//		             -- busca cod tabela
//		             -- 
//		             V_IPH_FCC_SEQ := NULL;
//		             V_IPH_FCF_SEQ := NULL;
//		             -- 
//		             open c_iph_tabela ( v_iph_pho_seq(v_indice)
//		                  				, v_iph_seq(v_indice) );
//		             fetch c_iph_tabela into v_iph_cod_tab, v_princ_apac, V_IPH_FCC_SEQ, V_IPH_FCF_SEQ;
//		             close c_iph_tabela;
//		             -- 
//		             -- não cobra nem aplica regras em itens associados ao código 1
//		             -- 
//		             if v_iph_cod_tab = 1 
//		             then
//		                -- 
//		                v_consistente := 'N';
//		                -- 
//		             end if;
//		             -- 
//		             exit when v_consistente = 'N';
//		             -- 
//		             regras_ok := fatk_pmr_rn.RN_PMRC_ATU_REGRAS (
//		                              reg_proc_amb.seq		  -- p_pmr_seq in number
//		                              ,reg_proc_amb.csp_cnv_codigo
//		                      		  ,reg_proc_amb.pac_codigo -- p_pac_codigo in number
//		                   			  ,v_iph_pho_seq(v_indice) -- p_iph_pho_seq in number
//		                  			  ,v_iph_seq(v_indice) 	  -- p_iph_seq in number
//		                  			  ,v_dt_fim_cpe -- início da comptência
//		                   			  ,p_previa
//									  ,'MAMA'); -- Paramentro default NULL utilizado somente por esta rotina do SISMAMA 
//		             --
//		             if not regras_ok 
//		             then 
//					         	 aghp_grava_mensagem(
//		             'Chamada RN_PMRC_ATU_REGRAS retorna false pmr='||reg_proc_amb.seq||' iph_seq='||v_iph_seq(v_indice)
//		              ,'FAT_NEY'); 
//		                -- 
//		                -- nao passou pelas regras de validação
//		                -- a insercao de erros quando não se encontra regra
//		                -- é feita na rotina de validacao das regras
//				        -- Inibido em jan/2008 - os procedimentos serão cobrados em BPA
//		    		    -- desebinido em fev/2008
//		    		    -- 
//		                -- fatk_sma_rn.RN_SMAP_ATU_SITUACAO (
//		                --   		p_previa, 'C', reg_proc_amb.seq );
//		                -- 
//		                -- v_consistente := 'N';
//		                -- 
//		             end if;
//		             -- 
//		             exit when p_previa <> 'S' and v_consistente = 'N';
//		             --
//		             v_cpe_competencia := to_date('01'||to_char(reg_proc_amb.cpe_mes,'00')
//		                            ||to_char(reg_proc_amb.cpe_ano,'0000'),'DDMMYYYY');
//		             -- 
//			         reg_valor := null;
//			         -- 
//		             open c_valores ( v_iph_pho_seq(v_indice)
//		                           ,v_iph_seq(v_indice)
//		                           ,v_cpe_competencia );
//		             fetch c_valores into reg_valor;
//		             close c_valores;
//		             --
//		             -- montando dados
//		             -- 
//			         open c_dados_paciente (reg_proc_amb.pac_codigo, reg_proc_amb.dthr_realizado);
//		             fetch c_dados_paciente into reg_pac;
//			         close c_dados_paciente;
//		             --
//		             open c_busca_cgc;
//		             fetch c_busca_cgc into v_cgc, v_razao_social, v_cdd_codigo;
//		             close c_busca_cgc;
//		             -- 
//		             open  cur_conv;
//		             fetch cur_conv into v_cod_ans;
//		             close cur_conv;
//		             --
//		             open c_busca_dt_solic (reg_proc_amb.ise_soe_seq);
//		             fetch c_busca_dt_solic into v_dt_coleta;
//			         close c_busca_dt_solic;
//		             --
//			         open c_busca_dt_recebe (reg_proc_amb.ise_soe_seq, reg_proc_amb.ise_seqp);
//		             fetch c_busca_dt_recebe into v_dt_recebe;
//		  	         close c_busca_dt_recebe;
//		             --
//		             open c_busca_resp (reg_proc_amb.ise_soe_seq, reg_proc_amb.ise_seqp);
//		             fetch c_busca_resp into v_cpf;
//		             close c_busca_resp;
//		             -- 
//				     V_SER_VIN_CODIGO_RET := NULL;
//				     V_SER_MATR_RET := NULL;
//				     v_cns_medico := FATC_BUSCA_CNS_RESP(reg_proc_amb.ser_matricula_resp, reg_proc_amb.ser_vin_codigo_resp,
//					      reg_proc_amb.ise_soe_seq, reg_proc_amb.ise_seqP, null, null,
//						V_SER_VIN_CODIGO_RET, V_SER_MATR_RET);			 
//		             v_nro_exame_2 := to_number(reg_proc_amb.ise_soe_seq||TO_CHAR(reg_proc_amb.ise_seqp, 'FM000'));
//		             -- 
//		             v_tipo_proc := rn_smac_ver_sismama (reg_proc_amb.ise_soe_seq, reg_proc_amb.ise_seqp);
//		             -- 
//		             -- insere resposta relacionadas ao pacientes nos resultados já dados conforme a tabela que está
//		             -- sendo processada
//		             -- 
//		             IF v_tipo_proc = 1
//		             then
//					 begin
//		                -- 
//						v_codigo := 'C_CNES';
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo,
//		                		 v_cod_ans,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//		                --
//						v_codigo := 'C_EXAME';                
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo,
//		                		 v_nro_exame_2,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//		                -- 
//						v_codigo := 'C_ID_APEL';
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo,
//		                		 null,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//		                -- 
//						v_codigo := 'C_ID_NOME';                
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo,
//		                		 reg_pac.nome,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//		                -- 
//						v_codigo := 'C_ID_NOMEMAE';
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo,
//		                		 reg_pac.nome_mae,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                		 
//		                -- 
//						v_codigo := 'D_ID_DTNASC';
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo,
//		                		 to_char(reg_pac.dt_nascimento,'yyyymmdd'),
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//		                -- 
//						v_codigo := 'C_ID_IDENT';                
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo,
//		                		 reg_pac.rg,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                		 
//		                -- 
//						v_codigo := 'C_ID_EMIS';
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo,
//		                		 reg_pac.oemrg,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                                		 
//		                --
//						v_codigo :=  'C_ID_UFIDE';
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo,
//		                		 reg_pac.ufrg,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                
//		                -- 
//						v_codigo := 'C_ID_CIC';
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo,
//		                		 lpad(reg_pac.cpf,11,'0'),
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                
//		                -- 
//						v_codigo := 'C_ID_ESCO';
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo,
//		                		 reg_pac.escolaridade,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                
//		                --
//						v_codigo := 'C_ID_ENDERECO'; 
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo,
//		                		 reg_pac.logradouro,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                                		 
//		                -- 
//						v_codigo := 'C_ID_NUMERO';
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo,
//		                		 reg_pac.nro,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                
//		                --
//						v_codigo :=  'C_ID_COMPLEM';
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo,
//		                		 reg_pac.compl,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                
//		                -- 
//						v_codigo := 'C_ID_BAIRRO';
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo,
//		                		 reg_pac.bairro,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);               
//		                -- 
//						v_codigo := 'C_ID_UF' ; 
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo,
//		                		 reg_pac.uf,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);               
//		                -- 
//						v_codigo := 'C_IBGE';
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo,
//		                		 reg_pac.ibge,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);               
//		                -- 
//						v_codigo := 'C_ID_CEP';
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo,
//		                		 reg_pac.cep,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);               
//		                -- 
//						v_codigo := 'C_ID_FONE';
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo,
//		                		 reg_pac.fone,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);               
//		                -- 
//						v_codigo := 'C_ID_SUS';
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo,
//		                		 reg_pac.nro_cartao_saude,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);               
//		                --
//						v_codigo :=  'C_US_UF';
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo,
//		                		 reg_pac.us_uf,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);               
//		                -- 
//						v_codigo := 'C_US_IBGE'; 
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo,
//		                		 4314902, -- Codigo IBGE com digito verificador modulo 10
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);               
//		                -- 
//						v_codigo := 'C_US_CNES';                
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo,
//		                		 v_cod_ans,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//		                -- 
//						v_codigo := 'C_US_NOME';
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo,
//		                		 v_razao_social,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                               
//		                -- 
//						v_codigo := 'C_PRON';
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo,
//		                		 reg_pac.prontuario,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                
//		                -- 
//						v_codigo := 'C_ID_SEXO';
//						-- 
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo,
//		                		 reg_pac.SEXO,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp); 		
//		                -- 
//						v_codigo := 'C_ID_RACACOR'; 
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo ,
//		                		 lpad(reg_pac.RACA,2,'0'),
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//		 			    --  	
//						v_codigo := 'C_PAT_CNS';
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo ,
//		                		 v_cns_medico,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);  
//					    --  	
//						v_codigo := 'C_PAT_CIC';
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo ,
//		                		 lpad(v_cpf,11,'0'),
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//					    --  	
//					    v_codigo := 'D_COL_US';
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo ,
//		                		 to_char(v_dt_coleta,'yyyymmdd'),
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//		                -- 
//					    v_codigo := 'D_RECEBE';						 
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo,
//		                		 to_Char(v_dt_recebe,'yyyymmdd'),
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//		                -- 
//					    v_codigo := 'D_LIBERA';
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo,
//		                		 to_char(reg_proc_amb.dthr_realizado,'yyyymmdd'),
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);	
//		                -- 
//					    v_codigo := 'C_ID_REFE';
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo,
//		                		 null,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//		                -- 										 							 								   							  				 						 						 				
//					    v_codigo := 'C_EXAMIN';
//		                fatk_sma_rn.rn_smap_insere_s02
//		                		(v_codigo,
//		                		 null,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//		                -- 
//		                for r_histo_cad in cur_histo_cad
//		                loop
//		                   -- 
//		                   v_achou_cad_res := 'N';
//		                   -- 
//		                   open  cur_histo_res_det
//		                   		(reg_proc_amb.ise_soe_seq,
//		                   		 reg_proc_amb.ise_seqp,
//		                   		 r_histo_cad.codigo);
//		                   fetch cur_histo_res_det into v_resposta;
//		                   if cur_histo_res_det%notfound then
//						      v_achou_cad_res := 'N';
//		                      fatk_sma_rn.rn_smap_insere_s02
//		                		(r_histo_cad.codigo,
//		                		 0,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                      
//		                      -- 					  
//		                   else
//		                      v_achou_cad_res := 'S';
//				              if r_histo_cad.codigo = 'C_CLI_GRAV' and v_resposta = '1'
//							  and reg_pac.SEXO = '1' then
//							     update ael_sismama_histo_res set resposta = '0'
//								  where current of cur_histo_res_det;	
//				              elsif r_histo_cad.codigo = 'C_CLI_LOCA' and v_resposta = 'oo' then
//							     update ael_sismama_histo_res set resposta = '00'
//								  where current of cur_histo_res_det;	 
//				              elsif r_histo_cad.codigo in 
//							  ( 'C_HIS_LINFO','C_HIS_LINFAV','C_HIS_LINFCO',
//							    'C_HIS_COAL','C_HIS_EXTR','C_NEO_MALIG') 
//							   and v_resposta is null then
//							     update ael_sismama_histo_res set resposta = '0'
//								  where current of cur_histo_res_det;					  						    
//		                      -- 
//		                      end if;
//						   end if;
//		                   close cur_histo_res_det;		   
//		                   -- 
//		                end loop;
//		                -- 
//		exception
//		 when others then
//		        raise_application_error(-20000,'Erro FATK_SMA_RN.RN_SMAP_ATU_GERA_ESP loop v_tipo_proc = 1'
//		                                       ||v_sqlerrm);
//		end;															 							 								   							  				 						 						 				
//					 elsif v_tipo_proc = 2	-- and	v_dt_coleta > to_date('030920091400','ddmmyyyyhh24mi')		
//		             then
//					 begin
//		                --
//						if v_dt_coleta < to_date('030920091400','ddmmyyyyhh24mi') then
//						   AELP_GERA_RESP_MAMA_new(reg_proc_amb.ise_soe_seq,reg_proc_amb.ise_seqp);
//						end if; 
//						--
//						v_codigo := 'C_CNES';
//		                fatk_sma_rn.rn_smap_insere_s04
//		                		(v_codigo,
//		                		 v_cod_ans,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//		                -- 
//						v_codigo := 'C_EXAME';                
//		                fatk_sma_rn.rn_smap_insere_s04
//		                		(v_codigo,
//		                		 v_nro_exame_2,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//		                -- 
//						v_codigo := 'C_ID_APEL';
//		                fatk_sma_rn.rn_smap_insere_s04
//		                		(v_codigo,
//		                		 null,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//		                -- 
//						v_codigo := 'C_ID_NOME';                
//		                fatk_sma_rn.rn_smap_insere_s04
//		                		(v_codigo,
//		                		 reg_pac.nome,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//		                -- 
//						v_codigo := 'C_ID_NOMEMAE';
//		                fatk_sma_rn.rn_smap_insere_s04
//		                		(v_codigo,
//		                		 reg_pac.nome_mae,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                		 
//		                -- 
//						v_codigo := 'D_ID_DTNASC';
//		                fatk_sma_rn.rn_smap_insere_s04
//		                		(v_codigo,
//		                		 to_char(reg_pac.dt_nascimento,'yyyymmdd'),
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//		                -- 
//						v_codigo := 'C_ID_IDENT';               
//		                fatk_sma_rn.rn_smap_insere_s04
//		                		(v_codigo,
//		                		 reg_pac.rg,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                		 
//		                --
//						v_codigo := 'C_ID_EMIS';
//		                fatk_sma_rn.rn_smap_insere_s04
//		                		(v_codigo,
//		                		 reg_pac.oemrg,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                                		 
//		                -- 
//						v_codigo := 'C_ID_UFIDE'; 
//		                fatk_sma_rn.rn_smap_insere_s04
//		                		(v_codigo,
//		                		 reg_pac.ufrg,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                
//		                --
//						v_codigo := 'C_ID_CIC'; 
//		                fatk_sma_rn.rn_smap_insere_s04
//		                		(v_codigo,
//		                		 lpad(reg_pac.cpf,11,'0'),
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                
//		                -- 
//						v_codigo := 'C_ID_ESCO';
//		                fatk_sma_rn.rn_smap_insere_s04
//		                		(v_codigo,
//		                		 reg_pac.escolaridade,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                
//		                -- 
//						v_codigo := 'C_ID_ENDERECO';
//		                fatk_sma_rn.rn_smap_insere_s04
//		                		(v_codigo,
//		                		 reg_pac.logradouro,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                                		 
//		                --
//						v_codigo := 'C_ID_NUMERO'; 
//		                fatk_sma_rn.rn_smap_insere_s04
//		                		(v_codigo,
//		                		 reg_pac.nro,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                
//		                -- 
//						v_codigo := 'C_ID_COMPLEM';
//		                fatk_sma_rn.rn_smap_insere_s04
//		                		(v_codigo,
//		                		 reg_pac.compl,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                
//		                -- 
//						v_codigo := 'C_ID_BAIRRO';
//		                fatk_sma_rn.rn_smap_insere_s04
//		                		(v_codigo,
//		                		 reg_pac.bairro,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);               
//		                -- 
//						v_codigo := 'C_ID_UF';
//		                fatk_sma_rn.rn_smap_insere_s04
//		                		(v_codigo,
//		                		 reg_pac.uf,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);               
//		                -- 
//						v_codigo := 'C_IBGE';
//		                fatk_sma_rn.rn_smap_insere_s04
//		                		(v_codigo,
//		                		 reg_pac.ibge,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);               
//		                --
//						v_codigo := 'C_ID_CEP'; 
//		                fatk_sma_rn.rn_smap_insere_s04
//		                		(v_codigo,
//		                		 reg_pac.cep,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);               
//		                --
//						v_codigo := 'C_ID_FONE'; 
//		                fatk_sma_rn.rn_smap_insere_s04
//		                		(v_codigo,
//		                		 reg_pac.fone,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);               
//		                -- 
//						v_codigo := 'C_ID_SUS';
//		                fatk_sma_rn.rn_smap_insere_s04
//		                		(v_codigo,
//		                		 reg_pac.nro_cartao_saude,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);               
//		                -- 
//						v_codigo := 'C_US_UF';
//		                fatk_sma_rn.rn_smap_insere_s04
//		                		(v_codigo,
//		                		 reg_pac.us_uf,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);               
//		                -- 
//						v_codigo := 'C_US_IBGE';
//		                fatk_sma_rn.rn_smap_insere_s04
//		                		(v_codigo,
//		                		 4314902,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);               
//		                -- 
//						v_codigo :=  'C_US_CNES';               
//		                fatk_sma_rn.rn_smap_insere_s04
//		                		(v_codigo,
//		                		 v_cod_ans,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//		                --
//						v_codigo := 'C_US_NOME'; 
//		                fatk_sma_rn.rn_smap_insere_s04
//		                		(v_codigo,
//		                		 v_razao_social,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                               
//		                -- 
//						v_codigo := 'C_PRON';
//		                fatk_sma_rn.rn_smap_insere_s04
//		                		(v_codigo,
//		                		 reg_pac.prontuario,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                
//		                --
//						v_codigo := 'C_ID_SEXO'; 
//		                fatk_sma_rn.rn_smap_insere_s04
//		                		(v_codigo,
//		                		 reg_pac.SEXO,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//						--
//						v_codigo := 'C_ID_RACACOR';
//		                fatk_sma_rn.rn_smap_insere_s04
//		                		(v_codigo,
//		                		 lpad(reg_pac.RACA,2,'0'),
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//					    --	
//					    v_codigo := 'C_PAT_CNS';
//		                fatk_sma_rn.rn_smap_insere_s04
//		                		(v_codigo,
//		                		 v_cns_medico,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//					    --	
//					    v_codigo :=	'C_PAT_CIC';					 
//		                fatk_sma_rn.rn_smap_insere_s04
//		                		(v_codigo,
//		                		 lpad(v_cpf,11,'0'),
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//					    --	
//					    v_codigo :=	'D_RECEBE';					 
//		                fatk_sma_rn.rn_smap_insere_s04
//		                		(v_codigo,
//		                		 to_Char(v_dt_recebe,'yyyymmdd'),
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//					    --	
//					    v_codigo :=	'D_EXAME';					 
//		                fatk_sma_rn.rn_smap_insere_s04
//		                		(v_codigo,
//		                		 to_Char(v_dt_recebe,'yyyymmdd'),
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);						 
//					    --	
//					    v_codigo :=	'D_LIBERA';					 
//		                fatk_sma_rn.rn_smap_insere_s04
//		                		(v_codigo,
//		                		 to_char(reg_proc_amb.dthr_realizado,'yyyymmdd'),
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//					    --	
//					    v_codigo := 'C_ID_REFE';						 
//		                fatk_sma_rn.rn_smap_insere_s04
//		                		(v_codigo,
//		                		 null,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//		                -- 
//						v_resposta_C_CLI_DIAG := '0';
//		                for r_mamo_cad in cur_mamo_cad
//		                loop
//		                   -- 
//		                   v_achou_cad_res := 'N';
//		                   -- 
//		                   open  cur_mamo_res_det
//		                   		(reg_proc_amb.ise_soe_seq,
//		                   		 reg_proc_amb.ise_seqp,
//		                   		 r_mamo_cad.codigo);
//		                   fetch cur_mamo_res_det into v_resposta;
//		                   if cur_mamo_res_det%notfound
//		                   then
//		                      v_achou_cad_res := 'N';
//						   else
//		                      v_achou_cad_res := 'S';
//							  if r_mamo_cad.codigo = 'C_ANA_MENOP_IDADE' and v_resposta = '0' then
//							     update ael_sismama_mamo_res set resposta = null
//								  where current of cur_mamo_res_det;
//							  elsif r_mamo_cad.codigo = 'C_CLI_DIAG' and v_resposta is null then
//							     update ael_sismama_mamo_res set resposta = '0'
//								  where current of cur_mamo_res_det;
//							  elsif r_mamo_cad.codigo = 'C_CLI_DIAG' and v_resposta = '3' then						  
//								  v_resposta_C_CLI_DIAG := '3';
//							  elsif r_mamo_cad.codigo = 'C_MAMO_RASTR' and v_resposta = '3' 						  
//								  AND v_resposta_C_CLI_DIAG = '3' 
//								  then
//		                              update ael_sismama_mamo_res set resposta = '0'
//								  where ise_soe_Seq = reg_proc_amb.ise_soe_seq
//								       and ise_seqp = reg_proc_amb.ise_seqp
//									   and s03_codigo = 'C_CLI_DIAG'
//								       and resposta = '3'
//										;
//							  elsif r_mamo_cad.codigo = 'C_ANA_GRAVIDA' and v_resposta = '0' then
//							     update ael_sismama_mamo_res set resposta = '2'
//								  where current of cur_mamo_res_det;																							  						  						  
//							  elsif r_mamo_cad.codigo in
//							     ('C_MICRO_FORM_01D','C_MICRO_FORM_02D','C_MICRO_FORM_03D',
//		                          'C_MICRO_DISTR_01D','C_MICRO_DISTR_02D','C_MICRO_DISTR_03D',
//		                          'C_MICRO_FORM_01E','C_MICRO_FORM_02E','C_MICRO_FORM_03E',
//		                          'C_MICRO_DISTR_01E','C_MICRO_DISTR_02E','C_MICRO_DISTR_03E',						  
//								  'C_LINF_AUX_D','C_LINF_AUX_E','C_CLI_DIAG',
//								  'C_CLI_DESC_DIR','C_CLI_DESC_ESQ',
//								  'C_ANA_RADIO_MDIR','C_ANA_RADIO_MESQ',
//								  'C_CON_DIA_CAT_E','C_CON_DIA_CAT_D','C_CON_RECOM_D','C_CON_RECOM_E') 		  
//							   and v_resposta is null then
//							     update ael_sismama_mamo_res set resposta = '0'
//								  where current of cur_mamo_res_det;							  						  
//							  elsif r_mamo_cad.codigo in ('C_ANA_RADIO_MDIR','C_ANA_RADIO_MESQ') and 
//							        length(v_resposta) > 1 then
//							     update ael_sismama_mamo_res set resposta = '3'
//								  where current of cur_mamo_res_det;	
//							  elsif substr(r_mamo_cad.codigo,1,9) = 'C_ANA_ANO'
//							   and v_resposta in ('0','00','000','0000') then
//							     update ael_sismama_mamo_res set resposta = null
//								  where current of cur_mamo_res_det;
//							  elsif r_mamo_cad.codigo in
//		                         ('C_NOD_LOC_01D', 	'C_NOD_LOC_02D', 	'C_NOD_LOC_03D',
//		                          'C_MICRO_LOC_01D',  	'C_MICRO_LOC_02D', 	'C_MICRO_LOC_03D',
//		                          'C_ASSI_FOC_LOC01D',    'C_ASSI_FOC_LOC02D',
//		                          'C_ASSI_DIFU_LOC01D',   'C_ASSI_DIFU_LOC02D',
//		                          'C_DIS_FOC_LOC01D',      'C_DIS_FOC_LOC02D',
//		                          'C_AR_DENS_LOC01D',     'C_AR_DENS_LOC02D',
//		                          'C_NOD_LOC_01E', 	'C_NOD_LOC_02E', 	'C_NOD_LOC_03E',
//		                          'C_MICRO_LOC_01E',  	'C_MICRO_LOC_02E', 	'C_MICRO_LOC_03E',
//		                          'C_ASSI_FOC_LOC01E',    'C_ASSI_FOC_LOC02E',
//		                          'C_ASSI_DIFU_LOC01E',   'C_ASSI_DIFU_LOC02E',
//		                          'C_DIS_FOC_LOC01E',      'C_DIS_FOC_LOC02E',
//		                          'C_AR_DENS_LOC01E',     'C_AR_DENS_LOC02E'						  
//								  )					   
//							   and nvl(v_resposta,0) = '0' then
//							     update ael_sismama_mamo_res set resposta = '00'
//								  where current of cur_mamo_res_det;							  											  					  
//							  end if;
//		                   end if;
//		                   close cur_mamo_res_det;
//		                   --
//					    
//		                   if v_achou_cad_res = 'N'
//		                   then
//						      v_resposta := 0;
//		                      if r_mamo_cad.codigo in ('C_ANA_RADIO_MDIR','C_ANA_RADIO_MESQ')
//							  or substr(r_mamo_cad.codigo,1,9) = 'C_ANA_ANO' then
//							     v_resposta := null;
//		                      elsif r_mamo_cad.codigo = 'C_ANA_GRAVIDA'	then
//							  	 v_resposta := '2'; 				 
//		                      elsif r_mamo_cad.codigo in
//		                         ('C_NOD_LOC_01D', 	'C_NOD_LOC_02D', 	'C_NOD_LOC_03D',
//		                          'C_MICRO_LOC_01D',  	'C_MICRO_LOC_02D', 	'C_MICRO_LOC_03D',
//		                          'C_ASSI_FOC_LOC01D',    'C_ASSI_FOC_LOC02D',
//		                          'C_ASSI_DIFU_LOC02D',   'C_ASSI_DIFU_LOC02D',
//		                          'C_DIS_FOC_LOC01D',      'C_DIS_FOC_LOC02D',
//		                          'C_AR_DENS_LOC01D',     'C_AR_DENS_LOC02D',
//		                          'C_NOD_LOC_01E', 	'C_NOD_LOC_02E', 	'C_NOD_LOC_03E',
//		                          'C_MICRO_LOC_01E',  	'C_MICRO_LOC_02E', 	'C_MICRO_LOC_03E',
//		                          'C_ASSI_FOC_LOC01E',    'C_ASSI_FOC_LOC02E',
//		                          'C_ASSI_DIFU_LOC02E',   'C_ASSI_DIFU_LOC02E',
//		                          'C_DIS_FOC_LOC01E',      'C_DIS_FOC_LOC02E',
//		                          'C_AR_DENS_LOC01E',     'C_AR_DENS_LOC02E'						  
//								  )	then	
//								  v_resposta := '00';						 
//						      end if;					  
//		                      -- 
//		                      fatk_sma_rn.rn_smap_insere_s04
//		                		(r_mamo_cad.codigo,
//		                		 v_resposta,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                      
//		                      -- 
//		                   end if;
//		                   -- 
//		                end loop;
//		                -- 
//		--exception
//		-- when others then
//		--        raise_application_error(-20000,'Erro 2='
//		--                                       ||sqlerrm);
//		end;															 							 								   							  				 						 						 				
//		             elsif v_tipo_proc = 3
//		             then
//					    begin
//		                --
//						AELP_GERA_RESP_CITO_new(reg_proc_amb.ise_soe_seq,reg_proc_amb.ise_seqp); 
//						--
//					    v_codigo := 'C_CNES';				
//		                fatk_sma_rn.rn_smap_insere_s06
//		                		(v_codigo,
//		                		 v_cod_ans,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//		                -- 
//					    v_codigo :=	'C_EXAME';			                
//		                fatk_sma_rn.rn_smap_insere_s06
//		                		(v_codigo,
//		                		 v_nro_exame_2,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//		                --
//					    v_codigo :=	'C_ID_APEL';			 
//		                fatk_sma_rn.rn_smap_insere_s06
//		                		(v_codigo,
//		                		 null,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//		                -- 
//					    v_codigo :=	'C_ID_NOME';			                
//		                fatk_sma_rn.rn_smap_insere_s06
//		                		(v_codigo,
//		                		 reg_pac.nome,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//		                --
//					    v_codigo :=	'C_ID_NOMEMAE';			 
//		                fatk_sma_rn.rn_smap_insere_s06
//		                		(v_codigo,
//		                		 reg_pac.nome_mae,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                		 
//		                --
//					    v_codigo :=	'D_ID_DTNASC';			 
//		                fatk_sma_rn.rn_smap_insere_s06
//		                		(v_codigo,
//		                		 to_char(reg_pac.dt_nascimento,'yyyymmdd'),
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//		                -- 
//					    v_codigo :=	'C_ID_IDENT';			                
//		                fatk_sma_rn.rn_smap_insere_s06
//		                		(v_codigo,
//		                		 reg_pac.rg,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                		 
//		                --
//						v_codigo := 'C_ID_EMIS';
//		                fatk_sma_rn.rn_smap_insere_s06
//		                		(v_codigo,
//		                		 reg_pac.oemrg,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                                		 
//		                --
//						v_codigo := 'C_ID_UFIDE';
//		                fatk_sma_rn.rn_smap_insere_s06
//		                		(v_codigo,
//		                		 reg_pac.ufrg,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                
//		                -- 
//					    v_codigo :=	'C_ID_CIC';			
//		                fatk_sma_rn.rn_smap_insere_s06
//		                		(v_codigo,
//		                		 lpad(reg_pac.cpf,11,'0'),
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                
//		                --
//					    v_codigo :=	'C_ID_ESCO';			 
//		                fatk_sma_rn.rn_smap_insere_s06
//		                		(v_codigo,
//		                		 reg_pac.escolaridade,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                
//		                --
//					    v_codigo :=	'C_ID_ENDERECO';			 
//		                fatk_sma_rn.rn_smap_insere_s06
//		                		(v_codigo,
//		                		 reg_pac.logradouro,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                                		 
//		                -- 
//					    v_codigo :=	'C_ID_NUMERO';			
//		                fatk_sma_rn.rn_smap_insere_s06
//		                		(v_codigo,
//		                		 reg_pac.nro,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                
//		                -- 
//					    v_codigo :=	'C_ID_COMPLEM';			
//		                fatk_sma_rn.rn_smap_insere_s06
//		                		(v_codigo,
//		                		 reg_pac.compl,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                
//		                -- 
//					    v_codigo :=	'C_ID_BAIRRO';		
//		                fatk_sma_rn.rn_smap_insere_s06
//		                		(v_codigo,
//		                		 reg_pac.bairro,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);               
//		                -- 
//					    v_codigo :=	'C_ID_UF';			
//		                fatk_sma_rn.rn_smap_insere_s06
//		                		(v_codigo,
//		                		 reg_pac.uf,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);               
//		                -- 
//					    v_codigo :=	'C_IBGE';			
//		                fatk_sma_rn.rn_smap_insere_s06
//		                		(v_codigo,
//		                		 reg_pac.ibge,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);               
//		                -- 
//					    v_codigo :=	'C_ID_CEP';			
//		                fatk_sma_rn.rn_smap_insere_s06
//		                		(v_codigo,
//		                		 reg_pac.cep,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);               
//		                -- 
//					    v_codigo :=	'C_ID_FONE';		
//		                fatk_sma_rn.rn_smap_insere_s06
//		                		(v_codigo,
//		                		 reg_pac.fone,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);               
//		                -- 
//					    v_codigo :=	'C_ID_SUS';			
//		                fatk_sma_rn.rn_smap_insere_s06
//		                		(v_codigo,
//		                		 reg_pac.nro_cartao_saude,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);               
//		                -- 
//					    v_codigo :=	'C_US_UF';			
//		                fatk_sma_rn.rn_smap_insere_s06
//		                		(v_codigo,
//		                		 reg_pac.us_uf,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);               
//		                -- 
//					    v_codigo :=		'C_US_IBGE';		 
//		                fatk_sma_rn.rn_smap_insere_s06
//		                		(v_codigo,
//		                		 4314902,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);               
//		                --         
//					    v_codigo :=	'C_US_CNES';			        
//		                fatk_sma_rn.rn_smap_insere_s06
//		                		(v_codigo,
//		                		 v_cod_ans,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//		                -- 
//					    v_codigo :=	'C_US_NOME';			
//		                fatk_sma_rn.rn_smap_insere_s06
//		                		(v_codigo,
//		                		 v_razao_social,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                               
//		                -- 
//					    v_codigo :=	'C_PRON';			
//		                fatk_sma_rn.rn_smap_insere_s06
//		                		(v_codigo,
//		                		 reg_pac.prontuario,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                
//		                --  
//					    v_codigo :=	'C_ID_SEXO';			
//		                fatk_sma_rn.rn_smap_insere_s06
//		                		(v_codigo,
//		                		 reg_pac.SEXO,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);  				
//						--
//					    v_codigo :=	'C_ID_RACACOR';			
//		                fatk_sma_rn.rn_smap_insere_s06
//		                		(v_codigo,
//		                		 lpad(reg_pac.RACA,2,'0'),
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//		                -- 
//					    v_codigo :=	'C_PAT_CNS';			
//		                fatk_sma_rn.rn_smap_insere_s06
//		                		(v_codigo,
//		                		 v_cns_medico,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//		                -- 
//					    v_codigo :=	'C_PAT_CIC';			
//		                fatk_sma_rn.rn_smap_insere_s06
//		                		(v_codigo,
//		                		 lpad(v_cpf,11,'0'),
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);
//		                -- 
//					    v_codigo :=	'D_COL_US';			
//		                fatk_sma_rn.rn_smap_insere_s06
//		                		(v_codigo,
//		                		 to_Char(v_dt_coleta,'yyyymmdd'),
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);	
//		                -- 
//					    v_codigo :=	'D_RECEBE';			
//		                fatk_sma_rn.rn_smap_insere_s06
//		                		(v_codigo,
//		                		 to_Char(v_dt_recebe,'yyyymmdd'),
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);	
//		                -- 
//					    v_codigo :=	'D_LIBERA';			
//		                fatk_sma_rn.rn_smap_insere_s06
//		                		(v_codigo,
//		                		 to_Char(reg_proc_amb.dthr_realizado,'yyyymmdd'),
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);	
//		                -- 
//					    v_codigo :=	'C_ID_REFE';			
//		                fatk_sma_rn.rn_smap_insere_s06
//		                		(v_codigo,
//		                		 null,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);								 						 							 						 								   				
//						--				
//		                for r_cito_cad in cur_cito_cad
//		                loop
//		                   -- 
//		                   v_achou_cad_res := 'N';
//		                   -- 
//		                   open  cur_cito_res_det
//		                   		(reg_proc_amb.ise_soe_seq,
//		                   		 reg_proc_amb.ise_seqp,
//		                   		 r_cito_cad.codigo);
//		                   fetch cur_cito_res_det into v_achou_cad_res;
//		                   if cur_cito_res_det%notfound
//		                   then
//		                      v_achou_cad_res := 'N';
//		                   end if;
//		                   close cur_cito_res_det;
//		                   -- 
//		                   if v_achou_cad_res = 'N'
//		                   then
//		                      -- 
//		                      fatk_sma_rn.rn_smap_insere_s06
//		                		(r_cito_cad.codigo,
//		                		 0,
//		                		 reg_proc_amb.ise_soe_seq,
//		                		 reg_proc_amb.ise_seqp);                      
//		                      -- 
//		                   end if;
//		                   -- 
//		                end loop;
//						exception
//		 when others then
//		        raise_application_error(-20000,'Erro FATK_SMA_RN.RN_SMAP_ATU_GERA_ESP loop v_tipo_proc = 3'
//		                                       ||v_sqlerrm);
//		end;	
//		                -- 										 							 								   							  				 						 						 				
//		             end if;
//		             --              
//		             -- insere FAT_ESPELHOS_SISMAMA - alias SMA
//		             --
//		             v_sma_seq     := fatC_get_fat_sma_sq1_nextval;             
//		             -- 
//		             if p_previa = 'S'
//		             then
//		                -- 
//		                v_data_previa_2 := sysdate;
//		                -- 
//		             else
//		                -- 
//		                v_data_previa_2 := null;
//		                -- 
//		             end if;
//		             -- 
//		             BEGIN
//		                insert into fat_espelhos_sismama
//		                		(seq,
//		                		 prontuario,
//		                		 nro_exame,
//		                		 cod_tabela,
//		                		 dt_liberacao,
//		                		 pmr_seq,
//		                		 unf_seq,
//		                		 cpe_dt_hr_inicio,
//		                		 cpe_modulo,
//		                		 cpe_mes,
//		                		 cpe_ano,
//		                		 ind_consistente,
//		                		 criado_em,
//		                		 valor_serv_hosp,
//		                		 valor_serv_prof,
//		                		 valor_anestesista,
//		                		 valor_sadt,
//		                		 valor_proc,
//		                		 fcc_seq,
//		                		 fcf_seq,
//		                		 iph_pho_seq,
//		                		 iph_seq,
//		                		 data_previa,
//		                		 ser_matricula,
//		                		 ser_vin_codigo)
//		                		values
//		                		(v_sma_seq,
//		                		 reg_pac.prontuario,
//		                		 v_nro_exame_2,
//		                		 v_iph_cod_tab,
//		                		 reg_proc_amb.dthr_realizado,
//		                		 reg_proc_amb.seq,
//		                		 reg_proc_amb.unf_seq,
//		                		 reg_proc_amb.cpe_dt_hr_inicio,
//		                		 'MAMA',
//		                		 reg_proc_amb.cpe_mes,
//		                		 reg_proc_amb.cpe_ano,
//		                		 'S',
//		                		 sysdate,
//		                		 reg_valor.vlr_serv_hospitalar,
//		                		 reg_valor.vlr_serv_profissional,
//		                		 reg_valor.vlr_anestesista,
//		                		 reg_valor.vlr_sadt,
//		                		 reg_valor.vlr_procedimento,
//		                		 v_iph_fcc_seq,
//		                		 v_iph_fcf_seq,
//		                		 v_iph_pho_seq(v_indice),
//		                		 v_iph_seq(v_indice),
//		                		 v_data_previa_2,
//		                		 agh_config.get_matricula,
//		                		 agh_config.get_vinculo);
//		             EXCEPTION
//		                WHEN OTHERS THEN
//		                     -- 
//		                     V_MSG := SQLERRM;                
//		                     -- 
//		                     INSERT INTO FAT_LOG_ERRORS 
//		                             (MODULO, 
//		                              ERRO, 
//		                              PROGRAMA,
//		                              CRIADO_EM, 
//		                              CRIADO_POR,
//		                              CTH_SEQ, 
//		                              CAP_ATM_NUMERO, 
//		                              CAP_SEQP, 
//		                              COD_ITEM_SUS_1, 
//		                              COD_ITEM_SUS_2,
//		                              DATA_PREVIA, 
//		                              ICA_SEQP, 
//		                              ICH_SEQP,
//		                              IPH_PHO_SEQ,
//		                              IPH_PHO_SEQ_REALIZADO,
//		                              IPH_SEQ, 
//		                              IPH_SEQ_REALIZADO, 
//		                              PAC_CODIGO, 
//		                              PHI_SEQ, 
//		                              PHI_SEQ_REALIZADO,
//		                              PMR_SEQ, 
//		                              PRONTUARIO )
//		                             VALUES 
//		                             ('MAMA', 
//		                              '002 - ERRO INSERI FAT_ESPELHOS_SISMAMA. ERRO: '||V_MSG,
//		                    		  'RN_SMAP_ATU_GERA_ESP',
//		                              SYSDATE, 
//		                              USER, 
//		                              NULL, 
//		                              NULL, 
//		                              NULL, 
//		                              NULL, 
//		                              NULL, 
//		                              v_data_previa,
//		                              NULL, 
//		                              NULL, 
//		                              NULL, 
//		                              NULL, 
//		                              NULL, 
//		                              NULL,
//		                              reg_proc_amb.pac_codigo, 
//		                              reg_proc_amb.phi_seq, 
//		                              NULL,
//		                              reg_proc_amb.seq, 
//		                              NULL );
//		             END;
//		             -- 
//		             if v_tipo_proc = 1
//		             then
//		                -- 
//		                open cur_dado_histo
//		                	(reg_proc_amb.ise_soe_seq,
//		                	 reg_proc_amb.ise_seqp);
//		                -- 
//		             elsif v_tipo_proc = 2
//		             then
//		                -- 
//		                open cur_dado_mamo
//		                	(reg_proc_amb.ise_soe_seq,
//		                	 reg_proc_amb.ise_seqp);
//		                -- 
//		             elsif v_tipo_proc = 3
//		             then
//		                -- 
//		                open cur_dado_cito
//		                	(reg_proc_amb.ise_soe_seq,
//		                	 reg_proc_amb.ise_seqp);
//		                -- 
//		             end if;             
//		             -- 
//		             v_sair_2 := 'N';
//		             -- 
//		             loop
//		                -- 
//		                if v_tipo_proc = 1
//		                then
//		                   -- 
//		                   fetch cur_dado_histo into 
//		                   		v_s02_seq, 
//		                   		v_s04_seq,
//		                   		v_s06_seq;
//		                   if cur_dado_histo%notfound
//		                   then
//		                      v_sair_2 := 'S';
//		                   end if;
//		                   -- 
//		                elsif v_tipo_proc = 2
//		                then
//		                   -- 
//		                   fetch cur_dado_mamo into 
//		                   		 v_s02_seq, 
//		                   		 v_s04_seq,
//		                   		 v_s06_seq;
//		                   if cur_dado_mamo%notfound
//		                   then
//		                      v_sair_2 := 'S';
//		                   end if;
//		                   -- 
//		                elsif v_tipo_proc = 3
//		                then
//		                   -- 
//		                   fetch cur_dado_cito into 
//		                   		 v_s02_seq, 
//		                   		 v_s04_seq,
//		                   		 v_s06_seq;
//		                   if cur_dado_cito%notfound
//		                   then
//		                      v_sair_2 := 'S';
//		                   end if;
//		                   -- 
//		                end if;
//		                -- 
//		                exit when v_sair_2 = 'S';
//		                -- 
//		                v_iem_seq := fatC_get_fat_iem_sq1_nextval;
//		                -- 
//		                -- insere FAT_ITENS_ESPELHO_SISMAMA - alias IEM
//		                --
//		                begin
//		                  insert into fat_itens_espelho_sismama
//		 							(seq,
//		 							 criado_em,
//		 							 sma_seq,
//		 							 s02_seq,
//		 							 s04_seq,
//		 							 s06_seq)
//		 							values
//		 							(v_iem_seq,
//		 							 sysdate,
//		 							 v_sma_seq,
//		 							 v_s02_seq,
//		 							 v_s04_seq,
//		 							 v_s06_seq);
//		                exception
//		                   when others then
//		                        -- 
//		                        V_MSG := SQLERRM;                   
//		                        -- 
//		                        INSERT INTO FAT_LOG_ERRORS 
//		                             (MODULO, 
//		                              ERRO, 
//		                              PROGRAMA,
//		                              CRIADO_EM, 
//		                              CRIADO_POR,
//		                              CTH_SEQ, 
//		                              CAP_ATM_NUMERO, 
//		                              CAP_SEQP, 
//		                              COD_ITEM_SUS_1, 
//		                              COD_ITEM_SUS_2,
//		                              DATA_PREVIA, 
//		                              ICA_SEQP, 
//		                              ICH_SEQP,
//		                              IPH_PHO_SEQ,
//		                              IPH_PHO_SEQ_REALIZADO,
//		                              IPH_SEQ, 
//		                              IPH_SEQ_REALIZADO, 
//		                              PAC_CODIGO, 
//		                              PHI_SEQ, 
//		                              PHI_SEQ_REALIZADO,
//		                              PMR_SEQ, 
//		                              PRONTUARIO )
//		                             VALUES 
//		                             ('MAMA', 
//		                              '003 - ERRO INSERT FAT_ITENS_ESPELHO_SISMAMA. ERRO: '||V_MSG,
//		                    		  'RN_SMAP_ATU_GERA_ESP',
//		                              SYSDATE, 
//		                              USER, 
//		                              NULL, 
//		                              NULL, 
//		                              NULL, 
//		                              NULL, 
//		                              NULL, 
//		                              v_data_previa,
//		                              NULL, 
//		                              NULL, 
//		                              NULL, 
//		                              NULL, 
//		                              NULL, 
//		                              NULL,
//		                              reg_proc_amb.pac_codigo, 
//		                              reg_proc_amb.phi_seq, 
//		                              NULL,
//		                              reg_proc_amb.seq, 
//		                              NULL );
//		                end;
//		                -- 
//		             end loop;
//		             -- 
//		             if v_tipo_proc = 1
//		             then
//		                -- 
//		                close cur_dado_histo;
//		                -- 
//		             elsif v_tipo_proc = 2
//		             then
//		                -- 
//		                close cur_dado_mamo;
//		                --
//		             elsif v_tipo_proc = 3
//		             then
//		                -- 
//		                close cur_dado_cito;
//		                --
//		             end if;
//		             --              
//		             -- Milena - temporário
//		             -- 
//			         -- Inibido em jan/2008 - os procedimentos serão cobrados em BPA
//			         -- desenibido em fev/2008 - Milena
//			         -- 
//			         BEGIN
//		               fatk_sma_rn.RN_SMAP_ATU_SITUACAO (
//		                    p_previa, 'P', reg_proc_amb.seq );
//		             EXCEPTION
//		               WHEN OTHERS THEN
//		                    -- 
//		                    V_MSG := SQLERRM;
//		                    --
//		                    INSERT INTO FAT_LOG_ERRORS 
//		                    			(MODULO, 
//		                    			 ERRO,
//		                    			 PROGRAMA,
//		                                 CRIADO_EM,
//		                                 CRIADO_POR,
//		                                 CTH_SEQ,
//		                                 CAP_ATM_NUMERO,
//		                                 CAP_SEQP,
//		                                 COD_ITEM_SUS_1,
//		                                 COD_ITEM_SUS_2,
//		                                 DATA_PREVIA, 
//		                                 ICA_SEQP, 
//		                                 ICH_SEQP, 
//		                                 IPH_PHO_SEQ,
//		                                 IPH_PHO_SEQ_REALIZADO, 
//		                                 IPH_SEQ, 
//		                                 IPH_SEQ_REALIZADO, 
//		                                 PAC_CODIGO,
//		                                 PHI_SEQ, 
//		                                 PHI_SEQ_REALIZADO, 
//		                                 PMR_SEQ, 
//		                                 PRONTUARIO )
//		                                VALUES (
//		                                 'MAMA', 
//		                                 '004 - ERRO AO ATUALIZAR SITUACAO: '||V_MSG,
//		                       		     'RN_SMAP_ATU_GERA_ESP',
//		                                 SYSDATE, 
//		                                 USER, 
//		                                 NULL, 
//		                                 NULL, 
//		                                 NULL, 
//		                                 NULL, 
//		                                 NULL, 
//		                                 v_data_previa,
//		                                 NULL, 
//		                                 NULL, 
//		                                 v_iph_pho_seq(v_indice), 
//		                                 NULL, 
//		                                 v_iph_seq(v_indice),
//		                                 NULL, 
//		                                 reg_proc_amb.pac_codigo, 
//		                                 reg_proc_amb.phi_seq, 
//		                                 NULL,
//		                                 reg_proc_amb.seq, 
//		                                 NULL );
//		                    --
//		                    -- fatk_EPS_rn.RN_EPSP_ATU_SITUACAO (
//		                    --      p_previa, 'C', reg_proc_amb.seq );
//		                    -- 
//		                    exit when p_previa <> 'S' and v_consistente = 'N';
//		                    -- 
//		             END;
//		             --
//		          else -- if v_qtd_itens > 0
//		             -- 
//		             v_iph_cod_tab  := 0;
//		             v_cav_cod_sus  := 0;
//		             v_ctc_tps_tipo := 0;
//		             v_gra_cod_sus  := 0;
//		             v_cfe_cod_sus  := 0;
//		             v_iph_pho_seq(v_indice) := 0;
//		             v_iph_seq(v_indice)  := 0;
//		             v_iph_qtd_item(v_indice) := 0;
//		             -- 
//		         end if;
//		         -- 
//		         exit when v_qtd_itens = 0 or v_indice = v_qtd_itens
//		                   or (p_previa <> 'S' and v_consistente = 'N');
//		         -- 
//		         v_indice := v_indice + 1;
//		         -- 
//		      end loop;
//		      --
//		      EXCEPTION
//		         WHEN OTHERS THEN
//				       v_sqlerrm := SQLERRM;
//					--                  aghp_envia_email ('A','fat_log_enc_previa.txt',
//		               aghp_envia_email ('T','erro:'||v_sqlerrm,
//		                   'Erro Processo Faturamento Sismama!','ncorrea@hcpa.ufrgs.br ' );
//		       raise_application_error(-20000,'Erro FATK_SMA_RN.RN_SMAP_ATU_GERA_ESP '
//		                                       ||v_sqlerrm);
//		              -- 
//		              V_MSG := SQLERRM;              
//		              -- 
//		              INSERT INTO FAT_LOG_ERRORS 
//		              (MODULO, 
//		               ERRO, 
//		               PROGRAMA,
//		               CRIADO_EM, 
//		               CRIADO_POR,
//		               CTH_SEQ,
//		               CAP_ATM_NUMERO,
//		               CAP_SEQP,
//		               COD_ITEM_SUS_1,
//		               COD_ITEM_SUS_2,
//		               DATA_PREVIA, 
//		               ICA_SEQP, 
//		               ICH_SEQP, 
//		               IPH_PHO_SEQ,
//		               IPH_PHO_SEQ_REALIZADO, 
//		               IPH_SEQ, 
//		               IPH_SEQ_REALIZADO, 
//		               PAC_CODIGO,
//		               PHI_SEQ, 
//		               PHI_SEQ_REALIZADO, 
//		               PMR_SEQ, 
//		               PRONTUARIO )
//		              VALUES 
//		              ('MAMA',v_codigo||' '|| 
//		               '005 - ERRO NAO PREVISTO. ERRO: '||V_MSG ,
//		               'RN_SMAP_ATU_GERA_ESP',
//		               SYSDATE, 
//		               USER, 
//		               NULL, 
//		               NULL, 
//		               NULL, 
//		               NULL, 
//		               NULL, 
//		               v_data_previa,
//		               NULL, 
//		               NULL, 
//		               v_iph_pho_seq(v_indice), 
//		               NULL, 
//		               v_iph_seq(v_indice),
//		               NULL, 
//		               reg_proc_amb.pac_codigo, 
//		               reg_proc_amb.phi_seq, 
//		               NULL,
//		               reg_proc_amb.seq, NULL );
//		   END;
//		   -- 
//		end loop;
//		-- 
//		close c_proc_amb;
//		 AGHP_ENVIA_EMAIL('A','FAT_NEY'
//		,'Execução de RN_SMAP_ATU_GERA_ESP concluida '
//		  ||' em '||TO_CHAR(SYSDATE,'dd/mm/yy hh24:mi')
//		 ,'ncorrea@hcpa.ufrgs.br');
//		       aghp_envia_email ('A','fat_log_enc_previa.txt',
//		                   'COMPLETOU Processo Faturamento Sismama!','ncorrea@hcpa.ufrgs.br '
//		                  ||'mdelazzeri@hcpa.ufrgs.br' );
//		-- 
//		SALVAR_PROCESS;
//		--
//		end;
	//
	}

}
