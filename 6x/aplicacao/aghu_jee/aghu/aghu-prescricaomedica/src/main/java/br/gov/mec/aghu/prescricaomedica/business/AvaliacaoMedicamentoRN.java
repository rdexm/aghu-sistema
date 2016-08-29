package br.gov.mec.aghu.prescricaomedica.business;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.configuracao.dao.AghUnidadesFuncionaisDAO;
import br.gov.mec.aghu.dominio.DominioIndRespAvaliacao;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoMedicamento;
import br.gov.mec.aghu.dominio.DominioTipoUsoMdtoAntimicrobia;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.MpmCidAtendimento;
import br.gov.mec.aghu.model.MpmJustificativaUsoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.VRapServidorConselho;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.prescricaomedica.dao.MpmCidAtendimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmJustifTrocaEsquemaTbDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmJustificativaUsoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.VMpmPrescrMdtosDAO;
import br.gov.mec.aghu.prescricaomedica.vo.AvaliacaoMedicamentoVO;
import br.gov.mec.aghu.registrocolaborador.dao.VRapServidorConselhoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.Dominio;
import br.gov.mec.aghu.core.dominio.DominioString;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Classe de negocios da estoria #45269.
 *  
 * @author rafael.silvestre
 */
@Stateless
public class AvaliacaoMedicamentoRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6928163522954404837L;
	
	private static final Log LOG = LogFactory.getLog(AvaliacaoMedicamentoRN.class);

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	private static final String HIFEN = " - ";
	
	private static final String PONTO_VIRGULA = "; ";
	private static final String ESPACO_DIAS = " dias";
	private static final String ESPACO_DIA = " dia";
	private static final String DS_IGUAL = "DS=";
	
	@Inject
	private MpmJustificativaUsoMdtoDAO mpmJustificativaUsoMdtoDAO;
	
	@Inject
	private MpmJustifTrocaEsquemaTbDAO mpmJustifTrocaEsquemaTbDAO;
	
	@Inject
	private VMpmPrescrMdtosDAO vMpmPrescrMdtosDAO;
	
	@Inject
	private AghUnidadesFuncionaisDAO aghUnidadesFuncionaisDAO; 
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ManterPrescricaoMedicaRN manterPrescricaoMedicaRN;
	
	@EJB
	private ManterAltaSumarioRN manterAltaSumarioRN;
	
	@Inject
	private MpmPrescricaoMdtoDAO mpmPrescricaoMdtoDAO;
	
	@Inject
	private VRapServidorConselhoDAO vRapServidorConselhoDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;
	
	@Inject
	private MpmCidAtendimentoDAO mpmCidAtendimentoDAO;
	
	/**
	 * Método principal. Executa consultas C2, C3 e C4. Converte objetos a serem passados para o relatorio.
	 */
	public AvaliacaoMedicamentoVO obterRelatorioAvaliacaoMedicamento(Integer pSolicitacao, Integer pMedMatCodigo) throws ApplicationBusinessException {
		AvaliacaoMedicamentoVO retorno = null;
		Object[] objeto = this.mpmJustificativaUsoMdtoDAO.obterAvaliacaoMedicamento(pSolicitacao);
		if (objeto != null) {
			retorno = construirAvaliacaoMedicamento(objeto);
			if (retorno != null) {
				retorno.setDescricao6(concatenarDescricao(retorno.getTetDescricao(), retorno.getTebDescricao(), HIFEN));
				retorno.setIdade(obterIdadeFormatada(retorno.getDtNascimento()));
				retorno.setLocalizacao(obterLocalizacao(retorno.getLtoLtoId(), retorno.getQrtNumero(), retorno.getUnfSeq()));
				retorno.setDtHrInternacao(obterDataInternacao(retorno.getIntSeq(), retorno.getAtuSeq(), retorno.getHodSeq()));
				retorno.setConvenio(obterConvenioAtendimento(retorno.getAtdSeq()));
				retorno.setEquipe(obterNomeServidor(retorno.getSerMatricula(), retorno.getSerVinCodigo()));
				retorno.setNroRegConselho(obterRegConselho(retorno.getSerMatricula(), retorno.getSerVinCodigo()));
				retorno.setCprSigla(obterSiglaServidor(retorno.getSerMatricula(), retorno.getSerVinCodigo()));
				retorno.setCfSolicitante(funcaoCfSolicitanteFormula(retorno.getJumSeq()));
				retorno.setDescricao3(obterDescricaoAvaliacao(pSolicitacao)); 
				Object[] complemento = this.vMpmPrescrMdtosDAO.obterComplementoAvaliacaoMedicamento(pMedMatCodigo, retorno.getAtdSeq(), retorno.getJumSeq());
				if (complemento != null) {
					retorno.setMedDescricaoEdit(obterMedDescricaoEdit(complemento));
					retorno.setDescricao5((String) complemento[20]);
					retorno.setObservacao((String) complemento[15]);
					retorno.setDuracaoTrat(obterDuracaoTratamentoFormatado((Short) complemento[11]));
					retorno.setDuracaoTratAprov(obterDuracaoTratamentoFormatado((Short) complemento[12]));
					retorno.setDtHrInicioTratamento((Date) complemento[13]);
					retorno.setDtHrParecer((Date) complemento[19]);
					retorno.setNomeAval(obterNomeServidor((Integer) complemento[16], (Short) complemento[17]));
					retorno.setNroRegConselhoAval(obterRegConselho((Integer) complemento[16], (Short) complemento[17]));
					retorno.setCprSiglaAval(obterSiglaServidor((Integer) complemento[16], (Short) complemento[17]));
				}
			}
		}
		return retorno;
	}
	
	/**
	 * Obtem duração do tratamento no formato "XX dia(s)".
	 */
	private String obterDuracaoTratamentoFormatado(Short duracao) {
		if (duracao != null) {
			StringBuffer sb = new StringBuffer(StringUtils.EMPTY);
			if (duracao.equals(0) || duracao.equals(0)) {
				return sb.append(duracao.toString()).append(ESPACO_DIA).toString();
			} else {
				return sb.append(duracao.toString()).append(ESPACO_DIAS).toString();
			}
		}
		return null;
	}

	/**
	 * Concatena uma serie de strings para formar a descrição médica editada.
	 */
	private String obterMedDescricaoEdit(Object[] complemento) {
		String retorno = (String) complemento[4];
		String observacaoItem = (String) complemento[5];
		retorno = concatenarDescricao(retorno, observacaoItem, PONTO_VIRGULA); 
		String unidDoseEdit = DS_IGUAL + (String) complemento[6];
		retorno = concatenarDescricao(retorno, unidDoseEdit, PONTO_VIRGULA); 
		String viaAdministracao = (String) complemento[7];
		retorno = concatenarDescricao(retorno, viaAdministracao, PONTO_VIRGULA); 
		String freqEdit = (String) complemento[8];
		retorno = concatenarDescricao(retorno, freqEdit, PONTO_VIRGULA); 
		String indUsoMdto = obterDescricaoDominioTipoUsoMdtoAntimicrobia((String) complemento[9]);
		retorno = concatenarDescricao(retorno, indUsoMdto, PONTO_VIRGULA); 
		return retorno;
	}
	
	/**
	 * Obtem descrição do DominioTipoUsoMdtoAntimicrobia ou retorna string vazia em caso de objeto nulo.
	 */
	private String obterDescricaoDominioTipoUsoMdtoAntimicrobia(String chave) {
		if (chave != null) {
			return DominioTipoUsoMdtoAntimicrobia.valueOf(chave).obterDescricaoDetalhadaTipo();
		}
		return null;
	}
	
	/**
	 * Obtem descrição do DominioTipoUsoMdtoAntimicrobia ou retorna string vazia em caso de objeto nulo #45250.
	 */
	private String obterDescricaoDetalhadaDominioTipoUsoMdtoAntimicrobia(String chave) {
		if (chave != null) {
			return DominioTipoUsoMdtoAntimicrobia.valueOf(chave).obterDescricaoDetalhadaTipo();
		}
		return null;
	}

	/**
	 * C2 - Consulta que retorna os dados de mpm_justif_troca_esquema_tbs e  mpm_tipo_justif_troca_esq
	 */
	private String obterDescricaoAvaliacao(Integer tebJumSeq) {
		Object[] objeto = this.mpmJustifTrocaEsquemaTbDAO.obterDescricaoAvaliacaoMedicamento(tebJumSeq);
		if (objeto != null) {
			return concatenarDescricao((String) objeto[1], (String) objeto[2], HIFEN); 
		}
		return null;
	}
	
	/**
	 * Retorna as duas strings concatenadas com o separador selecionado caso a segunda string seja diferente de nulo. 
	 */
	private String concatenarDescricao(String dsc1, String dsc2, String separador) {
		if (dsc1 != null) {
			StringBuffer sb = new StringBuffer(StringUtils.EMPTY);
			sb.append(dsc1);
			if (dsc2 != null && !StringUtils.isBlank(dsc2)) {
				return sb.append(separador).append(dsc2).toString();
			}
			return sb.toString();
		}
		return null;
	}
	
	/**
	 * Invoca a função mpmc_local_pac 
	 */
	private String obterLocalizacao(String ltoLtoId, Short qrtNumero, Short unfSeq) throws ApplicationBusinessException {
		AinLeitos leito = null;
		if (ltoLtoId != null) {
			leito = this.internacaoFacade.obterAinLeitosPorChavePrimaria(ltoLtoId);
		}
		AinQuartos quarto = null;
		if (qrtNumero != null) {
			quarto = this.internacaoFacade.obterAinQuartosPorChavePrimaria(qrtNumero);
		}
		AghUnidadesFuncionais unidadeFuncional = null;
		if (unfSeq != null) {
			unidadeFuncional = this.aghUnidadesFuncionaisDAO.obterPeloId(unfSeq);
		}
		return this.manterPrescricaoMedicaRN.obterResumoLocalPaciente(leito, quarto, unidadeFuncional);
	}
	
	private String obterLocalizacaoSemExcecao(String ltoLtoId, Short qrtNumero, Short unfSeq) {
		AinLeitos leito = null;
		if (ltoLtoId != null) {
			leito = this.internacaoFacade.obterAinLeitosPorChavePrimaria(ltoLtoId);
		}
		AinQuartos quarto = null;
		if (qrtNumero != null) {
			quarto = this.internacaoFacade.obterAinQuartosPorChavePrimaria(qrtNumero);
		}
		AghUnidadesFuncionais unidadeFuncional = null;
		if (unfSeq != null) {
			unidadeFuncional = this.aghUnidadesFuncionaisDAO.obterPeloId(unfSeq);
		}
		return this.manterPrescricaoMedicaRN.obterResumoLocalPacienteSemLancarExcecao(leito, quarto, unidadeFuncional);
	}
	
	/**
	 * Invoca a função mpmc_ver_dt_ini_atd 
	 */
	private Date obterDataInternacao(Integer intSeq, Integer atuSeq, Integer hodSeq) throws ApplicationBusinessException {
		return this.manterAltaSumarioRN.obterDataInternacao(intSeq, atuSeq, hodSeq);
	}
	
	/**
	 * Invoca a função mpmc_ver_convenio 
	 */
	private String obterConvenioAtendimento(Integer atdSeq) {
		return this.examesFacade.obterConvenioAtendimento(atdSeq);
	}

	/**
	 * Invoca a função mpmc_ida_ano_mes_dia 
	 */
	public String obterIdadeFormatada(Date dtNascimento) {
		String strDataNasc = obterIdadeAnoMesDiaFormat(dtNascimento);
		if(strDataNasc.startsWith("0 anos")){			
			return strDataNasc.replaceAll(" 0 meses", StringUtils.EMPTY).replaceAll("0 anos ", StringUtils.EMPTY);
		}else{
			return strDataNasc.replaceAll(" 0 meses", StringUtils.EMPTY);
		}
	}
	
	/**
	 * Essa funciona
	 * Invoca a função mpmc_ida_ano_mes_dia 
	 */
	private String obterIdadeAnoMesDiaFormat(Date dtNascimento) {
		StringBuilder sb = new StringBuilder();
		
		Integer anos = DateUtil.obterQtdAnosEntreDuasDatas(DateUtil.truncaData(dtNascimento), DateUtil.truncaData(new Date()));
		
		if (anos < 0) {
			anos = 0;
		}
		
		Integer meses = (DateUtil.obterQtdMesesEntreDuasDatas(DateUtil.truncaData(dtNascimento), DateUtil.truncaData(new Date())) - (anos * 12));
		Integer dias = (DateUtil.obterQtdDiasEntreDuasDatas(DateUtil.adicionaMeses(dtNascimento, ((anos * 12) + meses)), new Date()));

		if (anos > 1) {
			sb.append(anos).append(" anos ");
		} else if (anos == 1) {
			sb.append(anos).append(" ano ");
		}
		
		if (meses > 1) {
			sb.append(meses).append(" meses ");
		} else if (meses == 1) {
			sb.append(meses).append(" mês ");
		}
		
		if (anos <= 0) {
			if (dias > 1) {
				sb.append(dias).append(" dias");
			} else if (dias == 1) {
				sb.append(dias).append(" dia");
			}
		}
		return sb.toString();
	}
	
	/**
	 * Atribui valores para as variaveis do relatorio com os valores retornados de C3.
	 */
	private AvaliacaoMedicamentoVO construirAvaliacaoMedicamento(Object[] object) {
		AvaliacaoMedicamentoVO vo = new AvaliacaoMedicamentoVO();
		vo.setJumSeq((Integer) object[0]);
		vo.setGupSeq((Short) object[1]);
		vo.setGestante(obterDescricaoDominio(object[2]));
		vo.setFuncRenalComprometida(obterDescricaoDominio(object[3]));
		vo.setPacImunodeprimido(obterDescricaoDominio(object[4]));
		vo.setIndicacao((String) object[5]);
		vo.setInfeccaoTratar((String) object[6]);
		vo.setTratAntimicrobAnt((String) object[7]);
		vo.setInternacaoPrevia(obterDescricaoDominio(object[8]));
		vo.setInicioInfeccao(obterDescricaoDominio(object[9]));
		vo.setInfecRelProcedInvasivo(obterDescricaoDominio(object[10]));
		vo.setSondaVesicalDemora(obterDescricaoDominio(object[11]));
		vo.setPesoEstimado((BigDecimal) object[12]);
		vo.setCondutaBaseProtAssist(obterDescricaoDominio(object[13]));
		vo.setInsufHepatica(obterDescricaoDominio(object[14]));
		vo.setVantagemNsPadronizacao((String) object[15]);
		vo.setUsoCronicoPrevInt(obterDescricaoDominio(object[16]));
		vo.setCustoDiarioEstReal((BigDecimal) object[17]);
		vo.setRefBibliograficas((String) object[18]);
		vo.setDiagnostico((String) object[19]);
		vo.setEcog(obterDescricaoDominio(object[20]));
		vo.setIntencaoTrat(obterDescricaoDominio(object[21]));
		vo.setLinhaTrat(obterDescricaoDominio(object[22]));
		vo.setTratAntCirurgia((String) object[23]);
		vo.setTratAntRadio((String) object[24]);
		vo.setTratAntQuimio((String) object[25]);
		vo.setMesAnoUltCiclo((Date) object[26]);
		vo.setTratAntHormonio((String) object[27]);
		vo.setTratAntOutros((String) object[28]);
		vo.setCriadoEm((Date) object[29]);
		vo.setTipoInfeccao(obterDescricaoDominio(object[30]));
		vo.setSitAntibiograma(obterDescricaoDominio(object[31])); 
		vo.setNomeGerme((String) object[32]);
		vo.setSensibilidadeAntibiotico((String) object[33]);
		vo.setOrientacaoAvaliador(obterDescricaoDominio(object[34]));
		vo.setJustificativa((String) object[35]);
		vo.setSituacao(obterDescricaoDominio(object[36]));
		vo.setProntuario((Integer) object[37]);
		vo.setNome((String) object[38]);
		vo.setSexo(obterDescricaoDominio(object[39]));
		vo.setDtNascimento((Date) object[40]);
		vo.setLtoLtoId((String) object[41]);
		vo.setQrtNumero((Short) object[42]);
		vo.setUnfSeq((Short) object[43]); 
		vo.setIntSeq((Integer) object[44]); 
		vo.setAtuSeq((Integer) object[45]); 
		vo.setHodSeq((Integer) object[46]); 
		vo.setAtdSeq((Integer) object[47]); 
		vo.setSerMatricula((Integer) object[48]);
		vo.setSerVinCodigo((Short) object[49]);
		vo.setClcDescricao((String) object[50]);
		vo.setTetDescricao((String) object[51]);
		vo.setTebDescricao((String) object[52]);
		vo.setDtPrevAlta((Date) object[53]);
		vo.setResponsavelAvaliacao((DominioIndRespAvaliacao) object[54]);
		return vo;
	}

	/**
	 * Obtem a descrição correspondente ao Dominio equivalente.
	 */
	public String obterDescricaoDominio(Object object) {
		if (object != null) {
			if (object instanceof Dominio) {
				return ((Dominio) object).getDescricao();
			}
			
			if (object instanceof DominioString) {
				return ((DominioString) object).getDescricao();
			}
			
			if (object instanceof Boolean) {
				return DominioSimNao.getInstance((Boolean) object).getDescricao();
			}
		}
		return StringUtils.EMPTY;
	}
	
	/**
	 * F1 da estória #45269.
	 * Justificativa Antiga.
	 * @param indicacao
	 * @param infeccaoTratar
	 * @param diagnostico
	 * @return Boolean
	 */
	public Boolean justificativaAntiga(String indicacao, String infeccaoTratar, String diagnostico){
		
		if(StringUtils.isBlank(indicacao) && StringUtils.isBlank(infeccaoTratar) && StringUtils.isBlank(diagnostico)){
			return Boolean.TRUE;
		}else{
			return Boolean.FALSE;
		}
	}
	
	/**
	 * F2 da estória #45269.
	 * Uso Restri antimicrobiano = N
	 * @param gupSeq
	 * @param indicacao
	 * @return Boolean
	 * @throws ApplicationBusinessException 
	 */
	public Boolean usoRestriAntimicrobianoIgualNao(Short gupSeq, String indicacao) 
			throws ApplicationBusinessException{
		Short paramUsoRestrito = this.parametroFacade.buscarValorShort(AghuParametrosEnum.P_GRPO_USO_MDTO_UR);
		if(gupSeq.equals(paramUsoRestrito) && StringUtils.isNotBlank(indicacao)){
			return Boolean.TRUE;
		}else{
			return Boolean.FALSE;
		}
	}
	
	/**
	 * F3 da estória #45269.
	 * Uso Restri antimicrobiano = S
	 * @param gupSeq
	 * @param infeccaoTratar
	 * @return Boolean
	 * @throws ApplicationBusinessException 
	 */
	public Boolean usoRestriAntimicrobianoIgualSim(Short gupSeq, String infeccaoTratar) 
			throws ApplicationBusinessException{
		Short paramUsoRestrito = this.parametroFacade.buscarValorShort(AghuParametrosEnum.P_GRPO_USO_MDTO_UR);
		if(gupSeq.equals(paramUsoRestrito) && StringUtils.isNotBlank(infeccaoTratar)){
			return Boolean.TRUE;
		}else{
			return Boolean.FALSE;
		}
	}
	
	/**
	 * F4 da estória #45269.
	 * Nao padron antimicrobiano = N
	 * @param gupSeq
	 * @param indicacao
	 * @param infeccaoTratar
	 * @return Boolean
	 * @throws ApplicationBusinessException
	 */
	public Boolean naoPadronAntimicrobianoIgualNao(Short gupSeq, String indicacao, String infeccaoTratar) 
			throws ApplicationBusinessException{
		Short paramNaoSelecionado = this.parametroFacade.buscarValorShort(AghuParametrosEnum.P_GRPO_USO_MDTO_NS);
		if(gupSeq.equals(paramNaoSelecionado) && StringUtils.isNotBlank(indicacao) && StringUtils.isBlank(infeccaoTratar)){
			return Boolean.TRUE;
		}else{
			return Boolean.FALSE;
		}
	}
	
	/**
	 * F5 da estória #45269.
	 * Nao padron antimicrobiano = N
	 * @param gupSeq
	 * @param indicacao
	 * @param infeccaoTratar
	 * @return Boolean
	 * @throws ApplicationBusinessException
	 */
	public Boolean naoPadronAntimicrobianoIgualSim(Short gupSeq, String indicacao, String infeccaoTratar) 
			throws ApplicationBusinessException{
		Short paramNaoSelecionado = this.parametroFacade.buscarValorShort(AghuParametrosEnum.P_GRPO_USO_MDTO_NS);
		if(gupSeq.equals(paramNaoSelecionado) && StringUtils.isNotBlank(indicacao) && StringUtils.isNotBlank(infeccaoTratar)){
			return Boolean.TRUE;
		}else{
			return Boolean.FALSE;
		}
	}
	
	/**
	 * F6 da estória #45269.
	 * IND_QUIMIOTERAPICO = S
	 * @param diagnostico
	 * @return Boolean
	 */
	public Boolean indQuimioterapicoIgualSim(String diagnostico){
		
		if(StringUtils.isNotBlank(diagnostico)){
			return Boolean.TRUE;
		}else{
			return Boolean.FALSE;
		}
	}
	
	/**
	 * F7 da estória #45269.
	 * Atualizar a situação do medicamento.
	 * @param seqSolicitacao
	 * @param dataInicio
	 * @param dataFim
	 * @param indResponsavelAvaliacao
	 */
	public void atualizarSituacaoMedicamento(Integer seqSolicitacao, Date dataInicio, Date dataFim, DominioIndRespAvaliacao indResponsavelAvaliacao){
		List<MpmJustificativaUsoMdto> listaMpmJustificativaUsoMdto = this.mpmJustificativaUsoMdtoDAO
				.obterJustificativaUsoMedicamento(seqSolicitacao, dataInicio, dataFim, indResponsavelAvaliacao);
		
		if(listaMpmJustificativaUsoMdto != null && !listaMpmJustificativaUsoMdto.isEmpty()){
					
			for (MpmJustificativaUsoMdto mpmJustificativaUsoMdto : listaMpmJustificativaUsoMdto) {
				this.mpmJustificativaUsoMdtoDAO.obterJustificativaUsoMdto(mpmJustificativaUsoMdto.getSeq());
				mpmJustificativaUsoMdto.setSituacao(DominioSituacaoSolicitacaoMedicamento.T);
				this.mpmJustificativaUsoMdtoDAO.atualizar(mpmJustificativaUsoMdto);
			}
		}
	}
	
	/**
	 * @ORADB CF_SOLICITANTE
	 * F9 da estória #45269.
	 * @return String
	 * @throws ApplicationBusinessException 
	 */
	public String funcaoCfSolicitanteFormula(Integer curJumSeq) throws ApplicationBusinessException{
		List<MpmPrescricaoMdto> listaMpmPrescricaoMdto = this.mpmPrescricaoMdtoDAO.obterMpmPrescricaoMedicamentos(curJumSeq);
		String solicitante = null;
		if(listaMpmPrescricaoMdto != null && !listaMpmPrescricaoMdto.isEmpty()){
				if(listaMpmPrescricaoMdto.get(0).getServidorValidacao() != null){
					solicitante = this.prontuarioOnlineFacade.formataNomeProf(listaMpmPrescricaoMdto.get(0).getServidorValidacao()
							.getId().getMatricula(), listaMpmPrescricaoMdto.get(0).getServidorValidacao().getId().getVinCodigo());
				}else{
					solicitante = this.prontuarioOnlineFacade.formataNomeProf(listaMpmPrescricaoMdto.get(0).getServidor()
							.getId().getMatricula(), listaMpmPrescricaoMdto.get(0).getServidor().getId().getVinCodigo());
				}
		}
		
		return solicitante;
	}
	
	/**
	 * @ORADB AFAC_GET_NOME_SERV
	 * F10 da estória #45269.
	 * @param serMatricula
	 * @param serVinCodigo
	 * @return String
	 */
	public String obterNomeServidor(Integer serMatricula, Short serVinCodigo){
		String nome = null;
		
		List<VRapServidorConselho> listaVRapServidorConselho = this.vRapServidorConselhoDAO
				.obterServidorConselhoPorMatriculaVinculo(serMatricula, serVinCodigo);
		
		if(listaVRapServidorConselho != null && !listaVRapServidorConselho.isEmpty()){
				nome = listaVRapServidorConselho.get(0).getNome();
		}
		
		return nome;
	}
	
	/**
	 * @ORADB AFAC_GET_REG_SERV
	 * F11 da estória #45269.
	 * @param serMatricula
	 * @param serVinCodigo
	 * @return String
	 */
	public String obterRegConselho(Integer serMatricula, Short serVinCodigo){
		String nroRegConselho = null;
		
		List<VRapServidorConselho> listaVRapServidorConselho = this.vRapServidorConselhoDAO
				.obterServidorConselhoPorMatriculaVinculo(serMatricula, serVinCodigo);
		
		if(listaVRapServidorConselho != null && !listaVRapServidorConselho.isEmpty()){
				nroRegConselho = listaVRapServidorConselho.get(0).getNroRegConselho();
		}
		
		return nroRegConselho;
	}
	
	/**
	 * @ORADB AFAC_GET_SIGLA_SERV
	 * F12 da estória #45269.
	 * @param serMatricula
	 * @param serVinCodigo
	 * @return String
	 */
	public String obterSiglaServidor(Integer serMatricula, Short serVinCodigo){
		String sigla = null;
		
		List<VRapServidorConselho> listaVRapServidorConselho = this.vRapServidorConselhoDAO
				.obterServidorConselhoPorMatriculaVinculo(serMatricula, serVinCodigo);
		
		if(listaVRapServidorConselho != null && !listaVRapServidorConselho.isEmpty()){
				sigla = listaVRapServidorConselho.get(0).getSigla();
		}
		
		return sigla;
	}
	
	/**
	 * Método principal estória #45250. Executa consultas C1, C2, C3 e C4. Converte objetos a serem passados para o relatorio.
	 */
	public AvaliacaoMedicamentoVO obterRelatorioSolicitacaoMedicamentoAvaliar(MpmJustificativaUsoMdto jumSeq) throws ApplicationBusinessException {
		AvaliacaoMedicamentoVO retorno = null;
		Object[] objeto = this.mpmJustificativaUsoMdtoDAO.obterAvaliacaoMedicamento(jumSeq.getSeq()); //C1
		if (objeto != null) {
			retorno = construirAvaliacaoMedicamento(objeto);
			if (retorno != null) {
				retorno.setDescricao6(concatenarDescricao(retorno.getTetDescricao(), retorno.getTebDescricao(), HIFEN));
				retorno.setIdade(obterIdadeFormatada(retorno.getDtNascimento()));
				retorno.setLocalizacao(obterLocalizacaoSemExcecao(retorno.getLtoLtoId(), retorno.getQrtNumero(), retorno.getUnfSeq()));
				retorno.setDtHrInternacao(obterDataInternacao(retorno.getIntSeq(), retorno.getAtuSeq(), retorno.getHodSeq()));
				retorno.setConvenio(obterConvenioAtendimento(retorno.getAtdSeq()));
				retorno.setEquipe(obterNomeServidor(retorno.getSerMatricula(), retorno.getSerVinCodigo()));
				retorno.setNroRegConselho(obterRegConselho(retorno.getSerMatricula(), retorno.getSerVinCodigo()));
				retorno.setCprSigla(obterSiglaServidor(retorno.getSerMatricula(), retorno.getSerVinCodigo()));
				retorno.setCfSolicitante(funcaoCfSolicitanteFormula(retorno.getJumSeq()));
				retorno.setTitulo(retornarTextoTituloRelatorio(null, null, null));
				retorno.setDescricao3(obterDescricaoAvaliacao(jumSeq.getSeq())); //C4
				List<MpmCidAtendimento> listaCidAtendimento = mpmCidAtendimentoDAO.listarCidAtendimentoPorAtdSeq(retorno.getAtdSeq());//C3
				if (listaCidAtendimento != null && listaCidAtendimento.get(0) != null) {					
					retorno.setDescricao2(concatenarDescricaoDiagnostico(listaCidAtendimento, HIFEN));
				}
				List<Object[]> listaComplemento = this.vMpmPrescrMdtosDAO.obterDadosAvaliacaoMedicamento(retorno.getJumSeq()); //C2
				if (listaComplemento != null && !listaComplemento.isEmpty()) {
					StringBuffer medDescricaoEdit = new StringBuffer("");
					for (Object[] complemento : listaComplemento) {						
						medDescricaoEdit.append(obterMedDescricaoEditSolicitacaoMedAvaliar(complemento)).append(StringUtils.LF);
					}
					retorno.setMedDescricaoEdit(medDescricaoEdit.toString());
				}
			}
		}
		return retorno;
	}
	
	/**
	 * Concatena uma serie de strings para formar a descrição médica editada estória #45250.
	 */
	private String obterMedDescricaoEditSolicitacaoMedAvaliar(Object[] complemento) {
		String retorno = (String) complemento[1];
		String observacaoItem = (String) complemento[2];
		retorno = concatenarDescricao(retorno, observacaoItem, PONTO_VIRGULA); 
		String unidDoseEdit = DS_IGUAL + (String) complemento[3];
		retorno = concatenarDescricao(retorno, unidDoseEdit, PONTO_VIRGULA); 
		String viaAdministracao = (String) complemento[4];
		retorno = concatenarDescricao(retorno, viaAdministracao, PONTO_VIRGULA); 
		String freqEdit = (String) complemento[5];
		retorno = concatenarDescricao(retorno, freqEdit, PONTO_VIRGULA);
		String duracaoTrat = obterDuracaoTratamentoFormatado((Short) complemento[6]);
		retorno = concatenarDescricao(retorno, duracaoTrat, PONTO_VIRGULA);
		String indUsoMdto = obterDescricaoDetalhadaDominioTipoUsoMdtoAntimicrobia((String) complemento[7]);
		retorno = concatenarDescricao(retorno, indUsoMdto, PONTO_VIRGULA);

		return retorno;
	}
	
	/**
	 * Retorna as duas strings concatenadas com o separador selecionado caso a segunda string seja diferente de nulo. 
	 */
	private String concatenarDescricaoDiagnostico(List<MpmCidAtendimento> listaCidAtendimento, String separador) {
		String retorno = StringUtils.EMPTY;
		
		if(listaCidAtendimento.get(0).getCid() != null 
				&& listaCidAtendimento.get(0).getCid().getCid() != null 
				&& listaCidAtendimento.get(0).getCid().getCid().getSeq() != null){
			retorno = StringUtils.rightPad(retorno, 6);
			if(listaCidAtendimento.get(0).getCid().getCid() != null && listaCidAtendimento.get(0).getCid().getCid().getDescricao() !=null 
					&& !StringUtils.isBlank(listaCidAtendimento.get(0).getCid().getCid().getDescricao())){
				retorno = concatenarDescricao(retorno, listaCidAtendimento.get(0).getCid().getCid().getDescricao(), "");
			}
			retorno = retorno.concat(StringUtils.LF);
			
			if (listaCidAtendimento.get(0).getCid() != null && listaCidAtendimento.get(0).getCid().getCodigo() != null) {
				retorno = concatenarDescricao(retorno, StringUtils.rightPad(StringUtils.left(listaCidAtendimento.get(0).getCid().getCodigo(), 5), 5), "");
			}
			
			if (listaCidAtendimento.get(0).getCid().getDescricao() != null && !StringUtils.isBlank(listaCidAtendimento.get(0).getCid().getDescricao())) {
				retorno = concatenarDescricao(retorno, listaCidAtendimento.get(0).getCid().getDescricao(), " ");
			}
			
			if (listaCidAtendimento.get(0).getComplemento() != null && !StringUtils.isBlank(listaCidAtendimento.get(0).getComplemento())) {
				retorno = concatenarDescricao(retorno, listaCidAtendimento.get(0).getComplemento(), HIFEN);
			} else {
				retorno = concatenarDescricao(retorno, "", " ");
			}
			
		}else{
			
			if (listaCidAtendimento.get(0).getCid() != null && listaCidAtendimento.get(0).getCid().getCodigo() != null) {
				retorno = StringUtils.rightPad(StringUtils.left(listaCidAtendimento.get(0).getCid().getCodigo(), 5), 5);
			}
			
			if (listaCidAtendimento.get(0).getCid().getDescricao() != null && !StringUtils.isBlank(listaCidAtendimento.get(0).getCid().getDescricao())) {
				retorno = concatenarDescricao(retorno, listaCidAtendimento.get(0).getCid().getDescricao(), " ");
			}
			
			if (listaCidAtendimento.get(0).getComplemento() != null && !StringUtils.isBlank(listaCidAtendimento.get(0).getComplemento())) {
				retorno = concatenarDescricao(retorno, listaCidAtendimento.get(0).getComplemento(), HIFEN);
			} else {
				retorno = concatenarDescricao(retorno, "", " ");
			}
		}
		 
		return retorno;
	}
	
	/**
	 * @ORADB MPMR_SLCT_MDTO_AVAL.BEFOREREPORT
	 * Função para retornar texto do título do relatório Solicitação de Medicamentos a Avaliar.
	 * F8 da estória #45250.
	 * @param situacao
	 * @param dataInicial
	 * @param dataFinal
	 * @return String
	 */
	public String retornarTextoTituloRelatorio(DominioSituacao situacao, Date dataInicial, Date dataFinal){
		StringBuffer texto = new StringBuffer("");
		
		if(situacao != null){
			texto.append("- ").append(situacao.getDescricao());
		}
		
		if(dataInicial != null){
			if(StringUtils.isNotBlank(texto)){
				texto.append(", ");
			}else{
				texto.append("- ");
			}
			texto.append("de: ").append(new SimpleDateFormat("dd/MM/yyyy").format(dataInicial));
		}
		
		if(dataFinal != null){
			texto.append(" até: ").append(new SimpleDateFormat("dd/MM/yyyy").format(dataFinal));
		}else{
			texto.append(" até: ").append(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
		}
		
		return texto.toString();
	}
}