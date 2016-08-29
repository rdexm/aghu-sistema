package br.gov.mec.aghu.faturamento.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.vo.CursorCAtosMedVO;
import br.gov.mec.aghu.faturamento.vo.CursorCAtosProtVO;
import br.gov.mec.aghu.faturamento.vo.CursorCBuscaRegCivilVO;
import br.gov.mec.aghu.faturamento.vo.CursorCEAIVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class LayoutArquivoSusRN extends BaseBusiness {

    @EJB
    private FatcBuscaServClassRN fatcBuscaServClassRN;

    private static final Log LOG = LogFactory.getLog(LayoutArquivoSusRN.class);

    @Override
    @Deprecated
    protected Log getLogger() {
	return LOG;
    }

    private static final long serialVersionUID = -7826865220001805423L;

    public LayoutArquivoSusVO aplicarLayout(CursorCEAIVO vo) throws ApplicationBusinessException {
	final LayoutArquivoSusVO layout = new LayoutArquivoSusVO();

	layout.setvSeqp1(vo.getSeqp().toString());
	layout.setvParte10(inicializaVParte10(vo));
	layout.setvParte11(inicializaVParte11(vo));
	layout.setvParte12(inicializaVParte12(vo));
	layout.setvParte1(inicializaVParte1(vo));
	layout.setvParte3(inicializaVParte3(vo));

	return layout;
    }

    public LayoutArquivoSusVO aplicarLayout(CursorCAtosMedVO vo) {
	final LayoutArquivoSusVO layout = new LayoutArquivoSusVO();
	layout.setAtosMed(inicializaAtosMed(vo));
	return layout;
    }

    public LayoutArquivoSusVO aplicarLayout(CursorCBuscaRegCivilVO vo) {
	final LayoutArquivoSusVO layout = new LayoutArquivoSusVO();
	layout.setRegistroCivil(inicializaRegistroCivil(vo));
	return layout;
    }

    public LayoutArquivoSusVO aplicarLayout(CursorCAtosProtVO vo) {
	final LayoutArquivoSusVO layout = new LayoutArquivoSusVO();
	layout.setAtosProt(inicializaAtosProt(vo));
	return layout;
    }

    private String inicializaAtosProt(CursorCAtosProtVO vo) {
	return
		// lpad(to_char(nvl(IPH_COD_SUS,0)), 10, '0') -- Código OPM
		StringUtils.leftPad(vo.getCodigoOpm().toString(), 10, '0') +

		// ||lpad(nvl(seq_arq_sus,0),03,'0') -- linha do procedimento
		StringUtils.leftPad(vo.getLinhaProcedimento().toString(), 3, '0') +

		// ||lpad(nvl(reg_anvisa_opm,' '),20,' ') -- ANVISA
		StringUtils.leftPad(vo.getRegAnvisaOpm(), 20, ' ') +

		// ||lpad(nvl(serie_opm,' '),20,' ') -- Serie
		StringUtils.leftPad(vo.getSerieOpm(), 20, ' ') +

		// ||lpad(nvl(lote_opm,' '),20,' ') -- Lote
		StringUtils.leftPad(vo.getLoteOpm(), 20, ' ') +

		// ||lpad(nvl(nota_fiscal,'0'), 20, '0') -- Nota Fiscal
		StringUtils.leftPad(vo.getNotaFiscal().toString(), 20, '0') +

		// ||lpad(nvl(cgc,'0'),14,'0') -- CNPJ Fornecedor
		StringUtils.leftPad(vo.getCnpjFornecedor().toString(), 14, '0') +

		// ||lpad(nvl(cnpj_reg_anvisa,'0'),14,'0') -- CNPJ Fabricante
		StringUtils.leftPad(vo.getCnpjFabricante().toString(), 14, '0');
    }

    private String inicializaRegistroCivil(CursorCBuscaRegCivilVO vo) {
	return
	// lpad(NVL(pds.numero_dn,'0'),11,'0') --Número da DN
	StringUtils.leftPad(vo.getNumeroDn().toString(), 11, '0') +

	// || DECODE(GREATEST (TO_CHAR(pds.data_emissao, 'YYYY'),'2009'),
	// TO_CHAR(pds.data_emissao, 'YYYY'),
	// rpad(NVL(' ',' '),110,' '),
	// rpad(NVL(pac.nome,' '),70,' ') -- Nome do Recém Nascido
		(vo.getNomeRecemNascido().length() > 1 ? (StringUtils.rightPad(vo.getNomeRecemNascido(), 70, ' ') +
		// || rpad(NVL(pds.nome_cartorio,' '),20,' ') -- Razão Social
		// Cartório
			StringUtils.rightPad(vo.getRazaoSocialCartorio(), 20, ' ') +

			// || rpad(NVL(pds.livro,' '),8,' ') -- Livro
			StringUtils.rightPad(vo.getLivro(), 8, ' ') +

			// || lpad(NVL(pds.folhas,'0'),4,'0') -- Folha
			StringUtils.leftPad(vo.getFolhas().toString(), 4, '0') +

		// || lpad(NVL(pds.termo,'0'),8,'0')) -- Termo
		StringUtils.leftPad(vo.getTermo().toString(), 8, '0')

		)
			: StringUtils.rightPad(String.valueOf(' '), 110, ' ')) +

		// || rpad(NVL(TO_CHAR(pds.data_emissao, 'YYYYMMDD'),'0'), 8,
		// '0') -- Data de Emissão
		StringUtils.rightPad(vo.getDataEmissao(), 8, '0') +

		// || lpad(NVL(aam.seq_arq_sus,'0'),3,'0')
		StringUtils.leftPad(vo.getSeqArqSus().toString(), 3, '0')
		+

		// || DECODE (LENGTH(pac.reg_nascimento),30 , lpad('0',32,'0'),
		// lpad(NVL(pac.reg_nascimento,'0'),32,'0')) -- Marina
		// 05/02/2010
		(vo.getRegCivil().intValue() == 0 ? StringUtils.leftPad(String.valueOf(0), 32, '0') : StringUtils.leftPad(vo.getRegCivil()
			.toString(), 32, '0'));
    }

    private String inicializaAtosMed(CursorCAtosMedVO vo) {
	return
	// decode(greatest(COMPETENCIA_UTI,'201112'),'201112',1, 2) -- Indicador
	// documento
	vo.getIndicadorDocumento().toString() +

	// decode(greatest(COMPETENCIA_UTI,'201112'),'201112',lpad(nvl(CPF_CNS,'0'),15,'0'),
	// lpad(nvl(AIPC_GET_CNS_RESP(CPF_CNS),0), 15, '0'))
		StringUtils.leftPad(vo.getCpf().toString(), 15, '0') +

		// LPAD(NVL(decode(cbo,NULL,DECODE(FATC_BUSCA_CBO(P_CTH_SEQ,iph.seq,iph.pho_seq),NULL,
		// '0',FATC_BUSCA_CBO(P_CTH_SEQ,iph.seq,iph.pho_seq)),
		// CBO),0),6,'0') -- Marina 14/12/2009
		StringUtils.leftPad(vo.getCbo(), 6, '0') +

		// ||nvl(ind_equipe,0) -- equipe
		vo.getEquipe() +

		// ||decode(iph.fog_sgr_grp_seq,AGHC_OBTEM_PARAMETRO('P_GRUPO_OPM'),'3'||lpad(nvl(cgc,'0'),14,'0'),
		// '5'||lpad(nvl(P_CODIGO_DA_UPS,'0'), 14, '0'))
		vo.getCgc() +

		// ||'5' -- Indicador do documento do executor
		vo.getDocumentoExecutor() +

		// ||lpad(nvl(P_CODIGO_DA_UPS,'0'), 15, '0') -- CNES HCPA
		StringUtils.leftPad(vo.getCnesHcpa(), 15, '0') +

		// ||lpad(to_char(nvl(IPH_COD_SUS,0)), 10, '0') -- Cod
		// Procedimento
		StringUtils.leftPad(vo.getCodProcedimento().toString(), 10, '0')
		+

		// ||substr(lpad(to_char(nvl(QUANTIDADE,0)), 3, '0'),1,3) -- qtd
		(vo.getQuantidade().toString().length() > 3 ? vo.getQuantidade().toString().substring(0, 3) : StringUtils.leftPad(vo
			.getQuantidade().toString(), 3, '0')) +

		// ||rpad(nvl(competencia_uti,' '),6,' ')
		StringUtils.rightPad(vo.getCompetenciaUti(), 6, ' ') +

		// -- Marina 21/08/2012
		// -- || '000' -- serviço
		// -- || '000' -- classificação
		// -- Ney 2012/08/30
		// ||
		// FATC_BUSCA_serv_class(P_CTH_SEQ,aam.iph_seq,aam.iph_pho_seq)
		vo.getServClass();
	// -- || rpad('0',19,'0') -- filler
    }

    private String inicializaVParte10(CursorCEAIVO vo) {
	return
	// lpad(nvl(DCI_CODIGO_DCIH,'0'), 8, '0')|| --nu_lote
	StringUtils.leftPad(vo.getDciCodigoDcih(), 8, '0') +

	// rpad('0',3,'0')
		StringUtils.rightPad(vo.getQtLote(), 3, '0') +

		// lpad (to_char(add_months(to_date(lpad
		// (to_char(DCI_CPE_MES),2,'0')||
		// to_char(DCI_CPE_ANO),'MMYYYY'),1),'YYYYMM'), 6,'0')||
		// --apres_lote
		vo.getApresLote() +

		// rpad('0',3,'0')|| --seq_lote
		StringUtils.rightPad(vo.getSeqLote(), 3, '0') +

		// lpad(to_char(nvl(P_ORG_LOC_REC,0)), 10, ' ')|| --org_emis_aih
		StringUtils.leftPad(vo.getOrgEmisAih().toString(), 10, ' ') +

		// lpad(to_char(nvl(P_CNES_HCPA,0)), 7, '0')|| --CNES do HCPA
		StringUtils.leftPad(vo.getCnesHcpa().toString(), 7, '0') +

		// -- lpad(to_char(substr(P_ORG_LOC_REC,2,6)),6,'0')||
		// --Municipio do HCPA
		// '431490'||
		vo.getMunicipioInstituicao() +

		// lpad(to_char(nvl(NUMERO_AIH,0)), 13, '0') -- AIH
		StringUtils.leftPad(vo.getNumeroAih().toString(), 13, '0');
    }

    private String inicializaVParte11(CursorCEAIVO vo) {
		// lpad(to_char(nvl(TAH_SEQ,0)), 2, '0') -- Tipo AIH
		return StringUtils.leftPad(vo.getTahSeq().toString(), 2, '0');
    }

    private String inicializaVParte12(CursorCEAIVO vo) {
		return
		//  lpad(to_char(nvl(decode(ESPECIALIDADE_DCIH,5,87,ESPECIALIDADE_DCIH),0)), 2, '0')|| -- Esp da AIH
		StringUtils.leftPad(vo.getEspecialidadeDcih().toString().replace("5", "87"), 2, '0') +

		// rpad('0',45,'0')
			StringUtils.rightPad(String.valueOf(0), 45, '0');
    }
    
    @SuppressWarnings("PMD.NPathComplexity")
    private String inicializaVParte1(CursorCEAIVO vo) throws ApplicationBusinessException {
	final FatcBuscaServClassRN fatcBuscaServClassRN = getFatcBuscaServClassRN();

	return

	// lpad(to_char(fatc_busca_modalidade(iph_pho_seq_realiz,
	// iph_seq_realiz, data_internacao, data_saida)),2,'0')||
	StringUtils.leftPad(vo.getModalidade(), 2, '0') +

	// lpad(to_char(nvl(NRO_SEQAIH5,0)), 3, '0')|| -- seq da AIH5
		StringUtils.leftPad(vo.getNroSeqaih5().toString(), 3, '0') +

		// lpad(to_char(nvl(NUMERO_AIH_POSTERIOR,0)), 13, '0')|| -- AIH
		// Post
		StringUtils.leftPad(vo.getNumeroAihPosterior().toString(), 13, '0') +

		// lpad(to_char(nvl(NUMERO_AIH_ANTERIOR,0)), 13, '0')|| -- AIH
		// ant
		StringUtils.leftPad(vo.getNumeroAihAnterior().toString(), 13, '0') +

		// lpad(nvl(to_char(AIH_DTHR_EMISSAO, 'YYYYMMDD'),'0'), 8,
		// '0')|| -- Data AIH
		StringUtils.leftPad(vo.getAihDthrEmissao(), 8, '0') +

		// lpad(nvl(to_char(DATA_INTERNACAO, 'YYYYMMDD'),'0'), 8, '0')||
		// -- Data Int
		StringUtils.leftPad(vo.getDataInternacao(), 8, '0') +

		// lpad(nvl(to_char(DATA_SAIDA, 'YYYYMMDD'),'0'), 8, '0')|| --
		// Data saida
		StringUtils.leftPad(vo.getDataSaida(), 8, '0') +

		// lpad(to_char(nvl(IPH_COD_SUS_SOLIC,0)), 10, '0')|| -- Proc
		// solicitado
		StringUtils.leftPad(vo.getIphCodSusSolic().toString(), 10, '0') +

		// decode(nvl(IPH_COD_SUS_SOLIC,0),nvl(IPH_COD_SUS_REALIZ,0),'2','1')||
		// -- Mudança
		vo.getMudanca() +

		// lpad(to_char(nvl(IPH_COD_SUS_REALIZ,0)),10, '0')|| -- Proc
		// realizado
		StringUtils.leftPad(vo.getIphCodSusRealiz().toString(), 10, '0') +

		// lpad(to_char(nvl(TCI_COD_SUS,0)), 2, '0')|| -- caráter
		// internação
		StringUtils.leftPad(vo.getTciCodSus().toString(), 2, '0') +

		// lpad(nvl(MOTIVO_COBRANCA,'0'), 2, '0')|| -- motivo de saída (
		// Pos final 185)
		StringUtils.leftPad(vo.getMotivoCobranca(), 2, '0') +

		// decode(greatest(to_char(DATA_INTERNACAO,'YYYYMM'),'201112'),'201112',1,
		// 2) || -- tipo documento
		vo.getTipoDocumento().toString()
		+

		// decode(greatest(to_char(DATA_INTERNACAO,'YYYYMM'),'201112'),'201112',lpad(to_char(nvl(CPF_MEDICO_SOLIC_RESPONS,0)),
		// 15, '0'),
		// lpad(nvl(AIPC_GET_CNS_RESP(CPF_MEDICO_SOLIC_RESPONS),0), 15,
		// '0')) || -- cpf médico solicit.
		((String.valueOf(0).equals(vo.getCpfMedicoSolicRespons())) ? StringUtils.leftPad(String.valueOf(0), 15, '0') : vo
			.getCpfMedicoSolicRespons().toString())
		+

		// decode(greatest(to_char(DATA_INTERNACAO,'YYYYMM'),'201112'),'201112',1,
		// 2) || -- tipo documento
		vo.getTipoDocumento().toString()
		+

		// decode(greatest(to_char(DATA_INTERNACAO,'YYYYMM'),'201112'),'201112',lpad(to_char(nvl(CPF_MEDICO_SOLIC_RESPONS,0)),
		// 15, '0'),
		// lpad(nvl(AIPC_GET_CNS_RESP(CPF_MEDICO_SOLIC_RESPONS),0), 15,
		// '0')) || -- cpf médico respons.
		((String.valueOf(0).equals(vo.getCpfMedicoSolicRespons())) ? StringUtils.leftPad(String.valueOf(0), 15, '0') : vo
			.getCpfMedicoSolicRespons().toString()) +

		// decode(greatest(to_char(DATA_INTERNACAO,'YYYYMM'),'201112'),'201112',1,
		// 2) || -- tipo documento
		vo.getTipoDocumento().toString() +

		// decode(greatest(to_char(DATA_INTERNACAO,'YYYYMM'),'201112'),'201112',lpad(to_char(nvl(P_CPF_DIR_CLI,0)),
		// 15, '0'), lpad(nvl(AIPC_GET_CNS_RESP(P_CPF_DIR_CLI),0), 15,
		// '0')) || -- cpf diretor clínico
		((String.valueOf(0).equals(vo.getCpfDirCli())) ? StringUtils.leftPad(String.valueOf(0), 15, '0') : vo.getCpfDirCli()
			.toString()) +

		// decode(greatest(to_char(DATA_INTERNACAO,'YYYYMM'),'201112'),'201112',1,
		// 2) || -- tipo documento
		vo.getTipoDocumento().toString() +

		// decode(greatest(to_char(DATA_INTERNACAO,'YYYYMM'),'201112'),'201112',lpad(to_char(nvl(CPF_MEDICO_AUDITOR,0)),
		// 15, '0'), lpad(to_char(nvl(CNS_MEDICO_AUDITOR,0)), 15, '0'))
		// ||
		((String.valueOf(0).equals(vo.getCnsMedicoAuditor())) ? ((String.valueOf(0).equals(vo.getCpfMedicoAuditor())) ? StringUtils
			.leftPad(String.valueOf(0), 15, '0') : StringUtils.leftPad(vo.getCpfMedicoAuditor().toString(), 15, '0'))
			: ((String.valueOf(0).equals(vo.getCnsMedicoAuditor())) ? StringUtils.leftPad(String.valueOf(0), 15, '0')
				: StringUtils.leftPad(vo.getCnsMedicoAuditor().toString(), 15, '0'))) +

		// Rpad(nvl(CID_PRIMARIO,' '), 4)|| -- Diagnóstico Principal
		StringUtils.rightPad(vo.getCidPrimario(), 4) +

		// Ney 23/01/2015
		// Rpad(nvl(CID_SECUNDARIO,' '), 4)|| -- Diagnóstico Secundário
		//StringUtils.rightPad(vo.getCidSecundario(), 4) +

		// rpad(' ',4,' ')|| -- Diagnóstico Complementar
		//StringUtils.rightPad(String.valueOf(' '), 4) +

		// decode(substr(motivo_cobranca,1,1),4,Rpad(nvl(CID_PRIMARIO,' '),
		// 4),rpad(' ',4,' '))|| -- Cid Óbito
		//StringUtils.rightPad(vo.getCidObito(), 4, ' ') +
		// Fim Ney 23/01/2015
		
		// '000000000000' || -- Campos 33,34,35  
		"000000000000" +
		
		// '000' || -- filler
		vo.getFiller() +

		// Rpad(nvl(PAC_NOME,' '), 70)|| -- Nome
		StringUtils.rightPad(vo.getPacNome(), 70, ' ') +

		// lpad(nvl(to_char(PAC_DT_NASCIMENTO, 'YYYYMMDD'),' '), 8,
		// ' ')|| -- Data Nasc
		StringUtils.leftPad(vo.getPacDtNascimento(), 8, ' ') +

		// decode(pac_sexo,1,'M','F')|| -- Sexo
		vo.getPacSexo() +

		// lpad(nvl(PAC_COR,'99'), 2, '0')|| -- Cor/Raça
		StringUtils.leftPad(vo.getPacCor(), 2, '0') +

		// Rpad(nvl(PAC_NOME_MAE,' '), 70)|| -- Nome mãe
		StringUtils.rightPad(vo.getPacNomeMae(), 70, ' ') +

		// Rpad(nvl(AGHC_REM_CARAC_ESP(NOME_RESPONSAVEL_PAC,'',' '),'
		// '), 70)|| -- Nome responsável
		StringUtils.rightPad(fatcBuscaServClassRN.aghcRemoveCaracterEspecial(vo.getNomeResponsavelPac(), null, ' '), 70, ' ') +

		// to_char(nvl(IND_DOC_PAC,5))|| -- Tipo doc
		vo.getIndDocPac().toString() +

		// '0000' || -- Marina 26/10/2010 - Etnia Indigena --
		// '00000000000'
		vo.getEtniaIndigena() +

		// lpad(nvl(EXCLUSAO_CRITICA,'0'), 5, '0')|| -- Marina
		// 21/08/2012
		StringUtils.leftPad(vo.getExclusaoCritica(), 5, '0') +

		// '00' || -- Marina 21/08/2012
		"00" +

		// lpad(to_char(nvl(AIPC_GET_CARTAO_SUS(pac_prontuario),0)), 15,
		// '0')|| -- Cartão SUS
		StringUtils.leftPad(vo.getCartaoSUS().toString(), 15, '0') +

		// lpad(to_char(nvl(NACIONALIDADE_PAC,0)), 3, '0')|| --
		// Nacionalidade
		StringUtils.leftPad(vo.getNacionalidadePac().toString(), 3, '0') +

		// lpad(to_char(nvl(b.codigo_antigo,0)), 3, '0')|| -- Tipo de
		// Logradouro (END_TIP_CODIGO) recuperado de
		StringUtils.leftPad(vo.getEndTipCodigo().toString(), 3, '0') +

		// Rpad(nvl(AGHC_REM_CARAC_ESP(END_LOGRADOURO_PAC,'',' '),' '),
		// 50, ' ')|| -- Logradouro
		StringUtils.rightPad(fatcBuscaServClassRN.aghcRemoveCaracterEspecial(vo.getEndLogradouroPac(), null, ' '), 50, ' ') +

		// lpad(to_char(nvl(END_NRO_LOGRADOURO_PAC,'0')), 7, '0')|| --
		// Número
		StringUtils.leftPad(vo.getEndNroLogradouroPac().toString(), 7, '0') +

		// Rpad(nvl(AGHC_REM_CARAC_ESP(END_CMPL_LOGRADOURO_PAC,'',' '),'
		// '), 15, ' ')|| -- Complemento
		StringUtils.rightPad(fatcBuscaServClassRN.aghcRemoveCaracterEspecial(vo.getEndCmplLogradouroPac(), null, ' '), 15, ' ') +

		// Rpad(nvl(AGHC_REM_CARAC_ESP(END_BAIRRO_PAC,'',' '),' '), 30,
		// ' ')|| -- Bairro
		StringUtils.rightPad(fatcBuscaServClassRN.aghcRemoveCaracterEspecial(vo.getEndBairroPac(), null, ' '), 30, ' ') +

		// lpad(to_char(nvl(COD_IBGE_CIDADE_PAC,0)), 6, '0')|| -- Cidade
		// IBGE
		StringUtils.leftPad(vo.getCodIbgeCidadePac().toString(), 6, '0') +

		// Rpad(nvl(END_UF_PAC,' '), 2, ' ')|| -- UF
		StringUtils.rightPad(vo.getEndUfPac(), 2, ' ') +

		// lpad(to_char(nvl(END_CEP_PAC,0)), 8, '0')|| -- CEP
		StringUtils.leftPad(vo.getEndCepPac().toString(), 8, '0') +

		// lpad(to_char(nvl(PAC_PRONTUARIO,0)), 15, '0')|| -- Prontuario
		StringUtils.leftPad(vo.getPacProntuario().toString(), 15, '0') +

		// lpad(nvl(ENFERMARIA,'0001'), 4, '0')|| -- Enfermaria
		//StringUtils.leftPad(vo.getEnfermaria(), 4, '0') +

		//#54557
		"0000" +
		
		// lpad(nvl(LEITO,'0099'), 4, '0') -- Leito
		//StringUtils.leftPad(vo.getLeito(), 4, '0');
		
		//#54557
		"0000";
    }

    private String inicializaVParte3(CursorCEAIVO vo) {
	return
	// lpad(nvl(fatc_busca_dados_rn(P_CTH_SEQ),0),6,'0')|| -- saida
	// utineo,peso,meses
	StringUtils.leftPad(vo.getSaidaUtineo().toString(), 6, '0') +

	// rpad(' ',14,' ')|| -- CNPJ empreg
		StringUtils.rightPad(String.valueOf(' '), 14, ' ') +

		// rpad(' ',6,' ')|| -- CBOR
		StringUtils.rightPad(String.valueOf(' '), 6, ' ') +

		// rpad(' ',3,' ')|| -- CNAER
		StringUtils.rightPad(String.valueOf(' '), 3, ' ') +

		// rpad(' ',1,' ')|| -- Tipo Vínculo
		StringUtils.rightPad(String.valueOf(' '), 1, ' ') +

		// to_char(nvl(NASCIDOS_VIVOS,0))|| -- Qt_vivos
		vo.getNascidosVivos().toString() +

		// to_char(nvl(NASCIDOS_MORTOS,0))|| -- Qt_mortos
		vo.getNascidosMortos().toString() +

		// to_char(nvl(SAIDAS_ALTA,0))|| -- Qt_alta
		vo.getSaidasAlta().toString() +

		// to_char(nvl(SAIDAS_TRANSFERENCIA,0))|| -- Qt_transf
		vo.getSaidasTransferencia().toString() +

		// to_char(nvl(SAIDAS_OBITO,0))|| -- Qt Obito
		vo.getSaidasObito().toString() +

		// rpad('0',10,'0')||
		StringUtils.rightPad(String.valueOf(0), 10, '0') +

		// rpad('0',2,'0')|| -- qtde filhos
		StringUtils.rightPad(vo.getQteFilhos(), 2, '0') +

		// to_char(nvl(GRAU_INSTRUCAO_PAC,0))|| -- Grau de instrução
		vo.getGrauInstrucaoPac() +

		// rpad('0',4,'0')|| -- Cid Indicação
		StringUtils.rightPad(vo.getCidIndicacao(), 4, '0') +

		// rpad(' ',4,' ')|| -- Método contraceptivo
		StringUtils.rightPad(vo.getMetodoContraceptivo(), 4, ' ') +

		// rpad('1',1,'1')|| -- Gestação Alto Risco
		StringUtils.rightPad(vo.getGestacaoAltoRisco(), 1, '1') +

		// rpad(' ',35,'0')||
		StringUtils.rightPad(String.valueOf(' '), 35, '0') +

		// Prenatal -- Marina 11/11/2012 - alterado o tamanho de 11 para
		// 12 posiçoes - chamado: 84028
		// lpad(to_char(nvl(NRO_SISPRENATAL,0)), 12, '0')||
		StringUtils.leftPad(vo.getNroSisprenatal().toString(), 12, '0') + getColunaDocumento(vo) +

		// lpad(to_char(nvl(AIPC_GET_FONE_SISAIH(pac_prontuario),0)),
		// 11, '0')|| -- telefone paciente
		StringUtils.leftPad(vo.getPacTelefone(), 11, '0') +

		// -- Marina 21/08/2012
		// rpad(' ',50,' ')||
		StringUtils.rightPad(String.valueOf(' '), 50, ' ') +

		// -- Ney 23/01/2015
		//Rpad(nvl(CID_SECUNDARIO,' '), 4)||
		StringUtils.rightPad((String) CoreUtil.nvl(vo.getCidSecundario(), " "), 4) + // Diagnóstico Secundário - Marina  10/03/2015
		
		//decode(CID_SECUNDARIO, null, '0','1')
		(StringUtils.isBlank(vo.getCidSecundario()) ? "0" : "1") +
			
		/*
		    --'    0'|| -- Campos 100,101
		      '    0'|| -- Campos 102,103
		      '    0'|| -- Campos 104,105
		      '    0'|| -- Campos 106,107
		      '    0'|| -- Campos 108,109
		      '    0'|| -- Campos 110,111
		      '    0'|| -- Campos 112,113
		      '    0'|| -- Campos 114,115
    		  '    0'|| -- Campos 116,117
		*/
		"    0    0    0    0    0    0    0    0" +
         
        // rpad('0',165,'0')
        StringUtils.rightPad(String.valueOf(0), 165, '0') ;
		// -- Fim Ney 23/01/2015 
    }

    private String getColunaDocumento(CursorCEAIVO vo) {
	// -- Marina - 10/03/2010
	// --DECODE (IND_DOC_PAC, 1,
	// lpad(nvl(TO_CHAR(PAC_NRO_CARTAO_SAUDE),' '),32,' '),
	// lpad(nvl(PAC_NRO_CARTAO_SAUDE,'0'),32,'0')) ||
	// -- Marina 11/11/2013 - Melhoria AGHU - 31577
	// DECODE (IND_DOC_PAC, 1,
	// lpad(nvl(TO_CHAR(PAC_NRO_CARTAO_SAUDE),' '),32,' '), 2, lpad ('0',
	// (32 - length(PAC_RG)),'0') ||PAC_RG ,
	// lpad(nvl(PAC_NRO_CARTAO_SAUDE,'0'),32,'0')) ||
	int indDocPac = vo.getIndDocPac() != null ? vo.getIndDocPac().intValue() : 0;
	if (indDocPac == 1) {
	    String pacNroCartaoSaude = vo.getPacNroCartaoSaude2() != null ? vo.getPacNroCartaoSaude2() : String.valueOf(' ');
	    return StringUtils.leftPad(pacNroCartaoSaude, 32, ' ');

	} else if (indDocPac == 2) {
	    // Lpad('0',(32 - Length(:Pac_Rg)),'0')||:Pac_Rg
	    String pacRg = vo.getPacRG() != null ? vo.getPacRG() : String.valueOf('0');
	    return StringUtils.leftPad(pacRg, 32, '0');

	} else {
	    String pacNroCartaoSaude = vo.getPacNroCartaoSaude2() != null ? vo.getPacNroCartaoSaude2() : String.valueOf('0');
	    return StringUtils.leftPad(pacNroCartaoSaude, 32, '0');
	}
    }

    private FatcBuscaServClassRN getFatcBuscaServClassRN() {
	return fatcBuscaServClassRN;
    }

}
