package br.gov.mec.aghu.exames.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Months;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.configuracao.dao.AghHorariosUnidFuncionalDAO;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioColetaAtendUrgencia;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioResponsavelColetaExames;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoColeta;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.dominio.DominioTipoColeta;
import br.gov.mec.aghu.dominio.DominioTipoDia;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelPermissaoUnidSolicDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelTipoAmoExameConvDAO;
import br.gov.mec.aghu.exames.dao.AelTipoAmostraExameDAO;
import br.gov.mec.aghu.exames.solicitacao.business.ExameProvaCruzadaRN.ExameProvaCruzadaRNExceptionCode;
import br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameRN;
import br.gov.mec.aghu.exames.vo.FeriadoVO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicas;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelPermissaoUnidSolic;
import br.gov.mec.aghu.model.AelPermissaoUnidSolicId;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelTipoAmoExameConv;
import br.gov.mec.aghu.model.AelTipoAmoExameConvId;
import br.gov.mec.aghu.model.AelTipoAmostraExame;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghFeriados;
import br.gov.mec.aghu.model.AghHorariosUnidFuncional;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.ConvenioExamesLaudosVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ProvaCruzadaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ProvaCruzadaON.class);

	
	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IBancoDeSangueFacade bancoDeSangue;
	
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private SolicitacaoExameRN solicitacaoExameRN;
	
	@Inject 
	private AghHorariosUnidFuncionalDAO aghHorariosUnidFuncionalDAO;
	
	@Inject
	private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;
	
	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO; 
	
	@Inject
	private AelTipoAmoExameConvDAO aelTipoAmoExameConvDAO;
	
	@Inject
	private AelTipoAmostraExameDAO aelTipoAmostraExameDAO;

	@Inject
	private AelPermissaoUnidSolicDAO aelPermissaoUnidSolicDAO;
	
	private static final String FERIADO = "FER";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6722201834297521559L;

	public enum ProvaCruzadaONExceptionCode implements BusinessExceptionCode {
		MPM_02417
	}
	//@ORADB : AELC_HABILITA_PROVA_CRUZADA
	protected Boolean aelcHabilitaProvaCruzada() throws ApplicationBusinessException {
		String gerarProvaCruzada = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_GERA_PROVA_CRUZADA);
		if(gerarProvaCruzada == null) {
			return false;
		}
		
		return DominioSimNao.S.toString().equals(gerarProvaCruzada);
	}
	
	public DominioResponsavelColetaExames aelcVerificaRespColeta(Integer atdSeq, String siglaExame, Integer manSeq, DominioOrigemAtendimento origem) throws ApplicationBusinessException {
		ConvenioExamesLaudosVO convenioExamesLaudosVO = null;
		if(atdSeq != null) {
			convenioExamesLaudosVO = pacienteFacade.buscarConvenioExamesLaudos(atdSeq);
		}
		/*
		 *  -- Verifica  a responsabilidade pela coleta do material
		 *  -- por tipo de amostra, origem e convênio
		 */
		if(convenioExamesLaudosVO != null && convenioExamesLaudosVO.getCodigoConvenioSaudePlano() != null) {
			
			AelTipoAmoExameConvId id = new AelTipoAmoExameConvId();
			id.setTaeOrigemAtendimento(origem);
			id.setTaeEmaExaSigla(siglaExame);
			id.setTaeEmaManSeq(manSeq);
			id.setTaeManSeq(manSeq);
			id.setCspSeq(convenioExamesLaudosVO.getCodigoConvenioSaude());
			id.setCspCnvCodigo(Short.valueOf(convenioExamesLaudosVO.getCodigoConvenioSaudePlano()));

			List<AelTipoAmoExameConv> aelTipoAmostraExameConv = aelTipoAmoExameConvDAO.obterAelTipoAmoExameConvPorID(id);

			if (aelTipoAmostraExameConv != null && !aelTipoAmostraExameConv.isEmpty()) {
				return aelTipoAmostraExameConv.get(0).getResponsavelColeta();
			}
		}
		
		/*
		 * -- Verifica a responsabilidade pela coleta do material
		 * -- por tipo de amostra e origem
		 */
		List<AelTipoAmostraExame> aelTipoAmostraExame = aelTipoAmostraExameDAO.buscarAelTipoAmostraExameResponsavelColeta(siglaExame, manSeq, origem);
		if (aelTipoAmostraExame != null && !aelTipoAmostraExame.isEmpty()) {
			return aelTipoAmostraExame.get(0).getResponsavelColeta();
		}

		return DominioResponsavelColetaExames.N;
	}

	protected FeriadoVO aelpVerificaFeriado(Date p_dthr_programada) throws ApplicationBusinessException {
		AghFeriados feriado = aghuFacade.obterFeriado(DateUtil.truncaData(p_dthr_programada));
		String p_tipo_dia = null;
		
		if(feriado != null) {
			if(DominioTurno.M.equals(feriado.getTurno())) {
				String horaManhaFeriado = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HORA_MANHA_FERIADO);
				if(DateUtil.getHoras(p_dthr_programada) <= Integer.valueOf(horaManhaFeriado)) {
					p_tipo_dia = FERIADO;
				}
			}
			else if(DominioTurno.T.equals(feriado.getTurno())) {
				String horaManhaFeriado = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HORA_MANHA_FERIADO);
				if(DateUtil.getHoras(p_dthr_programada) > Integer.valueOf(horaManhaFeriado)) {
					p_tipo_dia = FERIADO;
				}
			}
			else if(DominioTurno.N.equals(feriado.getTurno())) {
				String horaManhaFeriado = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HORA_TARDE_FERIADO);
				if(DateUtil.getHoras(p_dthr_programada) > Integer.valueOf(horaManhaFeriado)) {
					p_tipo_dia = FERIADO;
				}
			}
			else {
				p_tipo_dia = FERIADO;
			}
		}
		else {
			p_tipo_dia = null;
		}
		
		return new FeriadoVO(feriado != null ? feriado.getTurno() : null, p_tipo_dia);
	}
	
	protected void aelpVerificaAtendColeta(String siglaExame, Integer manSeq, Short ufeUnfSeq, Short unfSeq, Integer atdSeq, DominioTipoColeta tipoColeta, String itemSolicitacao, 
			String p_a_coletar, Date p_dthr_programada ,Boolean coletaNaoAtende) throws ApplicationBusinessException {
		/*
		 * 	Verifica atendimento da coleta caso o pedido do exame seja Urgente e a situação
 			A Coletar
		 */
		if(DominioTipoColeta.N.equals(tipoColeta) || !StringUtils.equalsIgnoreCase(itemSolicitacao, p_a_coletar)) {
			coletaNaoAtende = false;
			return;
		}
		
		//Verifica se o atendimento é diferente dos SUS, pois nesses casos a coleta deve ser feita pelo coletador
		ConvenioExamesLaudosVO convenioExamesLaudosVO = pacienteFacade.buscarConvenioExamesLaudos(atdSeq);
		
		FatConvenioSaude convenioSaude = faturamentoFacade.obterConvenioSaude(convenioExamesLaudosVO.getCodigoConvenioSaude());
		if(convenioSaude == null) {
			coletaNaoAtende = false;
			return;
		}
		if(!DominioGrupoConvenio.S.equals(convenioSaude.getGrupoConvenio())) {
			coletaNaoAtende = false;
			return;
		}
		
		//Verifica se o exame e a unidade solicitante é atendida pela coleta totalmente ou somente em plantões quando solicitado na urgência
		AelPermissaoUnidSolic permissao = aelPermissaoUnidSolicDAO.obterPorChavePrimaria(new AelPermissaoUnidSolicId(siglaExame, manSeq, ufeUnfSeq, unfSeq));
		if(permissao == null) {
			coletaNaoAtende = false;
			return;
		}

		if(permissao.getColetaAtendeUrgencia() == null || DominioColetaAtendUrgencia.T.equals(permissao.getColetaAtendeUrgencia())) {
			coletaNaoAtende = false;
			return;
		}

		//Procedure de verificação de feriado
		Date v_dthr_programada = p_dthr_programada;
		FeriadoVO feriado = aelpVerificaFeriado(v_dthr_programada);
		feriado = verificarFeriado(feriado, p_dthr_programada, v_dthr_programada);

		AghHorariosUnidFuncional horarios = aghHorariosUnidFuncionalDAO.obterHorarioUnidadeFuncionalPor(permissao.getUnfSeqAvisa(), DominioTipoDia.getInstance(feriado.getTipoDia()) , p_dthr_programada);
		if(horarios == null) {
			coletaNaoAtende = false;
			return;
		}
		
		if(horarios.getIndPlantao()) {
			coletaNaoAtende = false;
		}
		else {
			coletaNaoAtende = true;
		}
	}
	
	// Extraído do método aelpVerificaAtendColeta() para evitar violação de PMD 
	private FeriadoVO verificarFeriado(FeriadoVO feriado, Date p_dthr_programada, Date v_dthr_programada) throws ApplicationBusinessException {
		if(feriado.getTipoDia() == null) {
			v_dthr_programada = DateUtil.adicionaDias(v_dthr_programada, 1);
			feriado = aelpVerificaFeriado(v_dthr_programada);
			
			String horaVesperaFeriado = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HORA_VESPERA_FERIADO);
			if(feriado.getTipoDia() != null && (feriado.getTurno() == null  || 
					( DominioTurno.M.equals(feriado.getTurno()) &&  DateUtil.getHoras(v_dthr_programada) <= Integer.valueOf(horaVesperaFeriado) ) )) {
				feriado.setTipoDia("VFE");
			}
			else {
				feriado.setTipoDia(DateUtil.dataToString(p_dthr_programada, "EEE"));
			}
		}
		return feriado;
	}
	
	/**
	 * @ORADB MPMC_LOCAL_PAC_MBC
	 * @param atdSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Short mbcLocalPacMbc(Integer atdSeq) throws ApplicationBusinessException {
		AghAtendimentos atendimento = aghuFacade.obterAghAtendimentoPorChavePrimaria(atdSeq);
		if(atendimento == null) {
			throw new ApplicationBusinessException(ProvaCruzadaONExceptionCode.MPM_02417);
		}
		
		List<MbcCirurgias> cirurgias = blocoCirurgicoFacade.listarLocalPacMbc(atendimento.getPaciente().getCodigo());
		if(cirurgias != null && !cirurgias.isEmpty()) {
			return cirurgias.get(0).getUnidadeFuncional().getSeq();
		}
		
		return atendimento.getUnidadeFuncional().getSeq();
	}
	
	protected Boolean aelcTemTipagemPendente(Integer atdSeq, DominioSituacaoColeta situacaoColeta) throws ApplicationBusinessException {
		Boolean v_eh_area_fechada = aghuFacade.possuiCaracteristicaPorUnidadeEConstante(mbcLocalPacMbc(atdSeq), ConstanteAghCaractUnidFuncionais.AREA_FECHADA_BANCO_DE_SANGUE);
		if(situacaoColeta != null && (
				
				((DominioSituacaoColeta.E.equals(situacaoColeta) || DominioSituacaoColeta.P.equals(situacaoColeta)) && v_eh_area_fechada)
				|| ((DominioSituacaoColeta.E.equals(situacaoColeta) || DominioSituacaoColeta.P.equals(situacaoColeta) || DominioSituacaoColeta.D.equals(situacaoColeta)) && !v_eh_area_fechada)
				)) {
			return true;
		}
		return false;
	}

	protected Boolean aelcTemProvaColetar(Integer atdSeq, String siglaExame, Short unfSeq, Integer manSeq, String codigo, Integer horasPct) throws ApplicationBusinessException {
		Boolean provaExame = false;
		//c_exame_prova
		List<AelItemSolicitacaoExames> listaExames = aelItemSolicitacaoExameDAO.obterListaItensExame(atdSeq, unfSeq, manSeq, siglaExame, codigo);
		if(listaExames != null && !listaExames.isEmpty()) {
			for(AelItemSolicitacaoExames item : listaExames) {
				if( (DateUtil.diffInDaysInteger(new Date(), item.getSolicitacaoExame().getCriadoEm())*24) <=  horasPct) {
					provaExame = true;
					break;
				}
			}
		}
		return provaExame;
	}

	protected Boolean aelcTemPctNeo(Integer atdSeq, String siglaExame, Short unfSeq, Integer manSeq, String codigo, Integer mesesPctNeo) {
		Boolean pctNeo = false;
		AghAtendimentos atendimento = aghuFacade.obterAghAtendimentoPorChavePrimaria(atdSeq);
		
		DateTime inicio = new DateTime(atendimento.getPaciente().getDtNascimento());		
		DateTime fim = new DateTime(new Date());
		Integer vMeses = Months.monthsBetween(inicio, fim).getMonths();
		
		if(vMeses <= mesesPctNeo) {
			List<AelItemSolicitacaoExames> listaExames = aelItemSolicitacaoExameDAO.obterListaItensExame(atdSeq, unfSeq, manSeq, siglaExame, codigo);
			if(listaExames != null && !listaExames.isEmpty()) {
				pctNeo = true;
			}
		}
		return pctNeo;
	}
	
	protected Boolean aelcPodeSolicitar(Integer atdSeq, String siglaExame, Integer manSeq, Short ufeUnfSeq, String itemSolicitacao, DominioSituacaoColeta situacaoColeta, 
			Integer horasPct, Integer mesesPctNeo) 
			throws ApplicationBusinessException {
		//Decide se não tem uma tipagem válida e não tem um exame a coletar para poder gerar um prova cruzada de tipagem sanguinea
		Boolean v_tem_tipagem_pendente = aelcTemTipagemPendente(atdSeq, situacaoColeta);
		Boolean v_tem_prova_a_coletar = aelcTemProvaColetar(atdSeq, siglaExame, ufeUnfSeq, manSeq, itemSolicitacao, horasPct);
		Boolean v_tem_pct_neo = aelcTemPctNeo(atdSeq, siglaExame, ufeUnfSeq, manSeq, itemSolicitacao, mesesPctNeo);
		
		if(!v_tem_tipagem_pendente) {
			return false;
		}
		if(v_tem_pct_neo) {
			return false;
		}
		if(!v_tem_prova_a_coletar) {
			return true;
		}
		return false;
	}
	
	protected AelItemSolicitacaoExames aelpIncluiSolItem(AghAtendimentos atendimento, String siglaExame, Short unfSeq, Integer manSeq, 
			String codigo, DominioTipoColeta tipoColeta,
			String nomeMicrocomputador,
			RapServidores servidorLogado, RapServidores responsavel) throws BaseException {
		return this.inserir(siglaExame, manSeq, unfSeq, atendimento, DominioSituacaoItemSolicitacaoExame.valueOf(codigo) , tipoColeta, nomeMicrocomputador, servidorLogado, responsavel);
	}
	
	public AelItemSolicitacaoExames inserir(String exaSigla, Integer manSeq, Short ufeUnfSeq,
			AghAtendimentos atendimento, 
			DominioSituacaoItemSolicitacaoExame situacao,
			DominioTipoColeta tipoColeta, String nomeMicrocomputador,
			RapServidores servidorLogado, RapServidores responsavel) throws BaseException {
		
		AelSolicitacaoExames soe = new AelSolicitacaoExames();
		soe.setAtendimento(atendimento);
		soe.setUnidadeFuncional(atendimento.getUnidadeFuncional());
		soe.setRecemNascido(Boolean.FALSE);
		soe.setCriadoEm(null);
		soe.setServidor(servidorLogado);
		soe.setInformacoesClinicas("Gerado automaticamente a partir da solicitação de hemocomponentes na prescrição médica.");
		soe.setServidorResponsabilidade(responsavel);
		
		AelSitItemSolicitacoes situacaoItem = this.examesFacade.obterSituacaoExamePeloId(situacao.getCodigo());
		
		AelUnfExecutaExames unfExec = this.examesFacade
				.obterAelUnidadeExecutoraExamesPorID(exaSigla, manSeq, ufeUnfSeq);
		AelExames exame = this.examesFacade.obterAelExamesPeloId(exaSigla);
		AelMateriaisAnalises material = examesFacade.obterMaterialAnalisePeloId(manSeq);
		AghUnidadesFuncionais unfFunc = aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(ufeUnfSeq);		
		if(unfFunc  == null){
			throw new ApplicationBusinessException(ExameProvaCruzadaRNExceptionCode.UFE_UNF_SEQ_INEXISTENTE,ufeUnfSeq);
		}
		
		AelItemSolicitacaoExames ise = new AelItemSolicitacaoExames();
		ise.setSituacaoItemSolicitacao(situacaoItem);
		ise.setAelUnfExecutaExames(unfExec);
		ise.setTipoColeta(tipoColeta);
		ise.setIndUsoO2(Boolean.FALSE);
		ise.setIndGeradoAutomatico(Boolean.FALSE);
		ise.setDthrProgramada(new Date());
		ise.setIndImprimiuTicket(Boolean.FALSE);
		ise.setIndCargaContador(Boolean.FALSE);
		ise.setUnfSeqAvisa(null);
		ise.setIndPossuiImagem(Boolean.FALSE);
		ise.setIndUsoO2Un(Boolean.FALSE);
		ise.setExame(exame);
		ise.setMaterialAnalise(material);
		ise.setUnidadeFuncional(unfFunc);
		
		soe.addItemSolicitacaoExame(ise);
		
		soe = this.solicitacaoExameRN.inserir(soe, nomeMicrocomputador, new Date(), servidorLogado);
		
		return soe.getItensSolicitacaoExame().get(0);
	}

	
	/**
	 * ORADB PACKAGE AELK_ISE_RN.RN_ISEP_ATU_UNF_AVIS
	 * 
	 * @param itemSolicitacaoExame
	 */
	protected Short atribuirUnfSeqAvisa(String siglaexame, Integer manSeq, Short unfSeq, Integer soeSeq) {
		
		AelSolicitacaoExames solicitacao = aelSolicitacaoExameDAO.obterPorChavePrimaria(soeSeq);
		
		AelPermissaoUnidSolic permissao = aelPermissaoUnidSolicDAO
		.buscarAelPermissaoUnidSolicPorEmaExaSiglaEmaManSeqUnfSeqUnfSeqSolicitante(
				siglaexame,
				manSeq,
				unfSeq,
				solicitacao.getUnidadeFuncional().getSeq());
		if (permissao != null && permissao.getUnfSeqAvisa() != null) {
			return permissao.getUnfSeqAvisa().getSeq();
		}
		return null;
	}

	
	public Boolean gerarPCT(Integer atdSeq, Boolean urgente, 
			DominioSituacaoColeta situacaoColeta,
			Integer shaSeq,
			String nomeMicrocomputador,
			RapServidores servidorLogado, RapServidores responsavel) throws BaseException {
		Boolean gerouProvaCruzada = true;
		String itemSolicitacao = null;
		DominioTipoColeta tipoColeta = null;
		Boolean coletaNaoAtende = null;
		
		if(!aelcHabilitaProvaCruzada()) {
			return true;
		}
		AghAtendimentos atendiemnto = aghuFacade.obterAghAtendimentoPorChavePrimaria(atdSeq);

		
		//@ORADB : AELP_BUSCA_PARAMETROS_UFE
		String v_exame_prova_cruzada = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_EXAME_PROVA_CRUZADA);
		Integer  v_material_prova_cruzada = parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_MATERIAL_PROVA_CRUZADA);
		Short v_unid_exec_prova_cruzada = parametroFacade.buscarValorShort(AghuParametrosEnum.P_UNID_EXEC_PROVA_CRUZADA);
		
		//@ORADB : AELP_BUSCA_PARAM_EXAME
		String p_coletado_solic = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_SITUACAO_COLETADO_SOLIC);
		String p_a_coletar = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_SITUACAO_A_COLETAR);
		String p_cancelado = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_SITUACAO_CANCELADO);
		Integer p_horas_pct_valido = parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_VALIDADE_AMOSTRA_PCT);
		Integer p_meses_pct_neo = parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_MESES_PCT_NEO);
		
		//busca o responsavel pela coleta
		DominioResponsavelColetaExames responsavelColeta = aelcVerificaRespColeta(atdSeq, v_exame_prova_cruzada, v_material_prova_cruzada, atendiemnto.getOrigem());
		
		//determinando a situação que o exame deverá ter quando o responsável pela coleta for o próprio solicitante
		if(DominioResponsavelColetaExames.S.equals(responsavelColeta)) {
			itemSolicitacao = p_coletado_solic;
		}
		//determinando a situação que o exame deverá ter quando o responsável pela coleta for o coletador
		//aelc_busca_tipo_coleta
		if(urgente) {
			tipoColeta = DominioTipoColeta.U;
		}
		else {
			tipoColeta = DominioTipoColeta.N;
		}
		
		if(DominioResponsavelColetaExames.C.equals(responsavelColeta)) {
			coletaNaoAtende = false;
			
			aelpVerificaAtendColeta(v_exame_prova_cruzada, v_material_prova_cruzada, v_unid_exec_prova_cruzada, 
					atendiemnto.getUnidadeFuncional().getSeq(), atendiemnto.getSeq(), tipoColeta, itemSolicitacao, p_a_coletar, new Date(), coletaNaoAtende);
			
			if(Boolean.TRUE.equals(coletaNaoAtende)) {
				itemSolicitacao = p_coletado_solic;
			}
			else {
				itemSolicitacao = p_a_coletar;
			}
		}
	
		Boolean podeSolicitar = aelcPodeSolicitar(atdSeq, v_exame_prova_cruzada, v_material_prova_cruzada, v_unid_exec_prova_cruzada, p_cancelado, situacaoColeta, p_horas_pct_valido, p_meses_pct_neo);
		if(!podeSolicitar) {
			return false;
		}
		/*		
		-- se a solicitação de hemoterapia é oriunda da Emergência Térreo então
		--    tipo de coleta sempre deverá ser urgente ('U')
		--
		-- está sendo trocado o tipo de coleta de 'U' (urgente) para 'N' (normal ou
		-- rotina) para que não dê erro quando a coleta não atende urgencia no
		-- momento da soliitação exames que aquela unidade solicitante
		*/
		if((aghuFacade.possuiCaracteristicaPorUnidadeEConstante(atendiemnto.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.ATEND_EMERG_TERREO) 
				&& DominioTipoColeta.N.equals(tipoColeta))
				|| 
				aghuFacade.possuiCaracteristicaPorUnidadeEConstante(atendiemnto.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.UNID_CTI)) {
			tipoColeta = DominioTipoColeta.U;
		} else if (DominioTipoColeta.U.equals(tipoColeta) && itemSolicitacao == p_coletado_solic) {
			tipoColeta = DominioTipoColeta.N;
		}
		
		AelItemSolicitacaoExames ise = aelpIncluiSolItem(atendiemnto, v_exame_prova_cruzada, v_unid_exec_prova_cruzada, v_material_prova_cruzada, 
				itemSolicitacao, tipoColeta, nomeMicrocomputador, servidorLogado, responsavel);
		
		AbsSolicitacoesHemoterapicas solHemo = bancoDeSangue.obterSolicitacoesHemoterapicas(atendiemnto.getSeq(), shaSeq);
		if(solHemo != null) {
			solHemo.setItemSolicitacaoExame(ise);
			bancoDeSangue.atualizarSolicitacaoHemoterapica(solHemo, nomeMicrocomputador);
		}
		gerouProvaCruzada = true;
		
		return gerouProvaCruzada;
	}
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

}
