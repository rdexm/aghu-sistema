package br.gov.mec.aghu.perinatologia.business;

import java.math.BigDecimal;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.casca.service.ICascaService;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.exames.service.IExamesService;
import br.gov.mec.aghu.exames.vo.DadosExameMaterialAnaliseVO;
import br.gov.mec.aghu.exames.vo.ExameMaterialAnaliseVO;
import br.gov.mec.aghu.exames.vo.ExameSignificativoVO;
import br.gov.mec.aghu.model.McoExameExterno;
import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.model.McoGestacoesId;
import br.gov.mec.aghu.model.McoGestacoesJn;
import br.gov.mec.aghu.model.McoResultadoExameSignifs;
import br.gov.mec.aghu.paciente.service.IPacienteService;
import br.gov.mec.aghu.perinatologia.dao.McoExameExternoDAO;
import br.gov.mec.aghu.perinatologia.dao.McoGestacoesDAO;
import br.gov.mec.aghu.perinatologia.dao.McoGestacoesJnDAO;
import br.gov.mec.aghu.perinatologia.dao.McoResultadoExameSignifsDAO;
import br.gov.mec.aghu.perinatologia.vo.DadosGestacaoVO;
import br.gov.mec.aghu.perinatologia.vo.ExameResultados;
import br.gov.mec.aghu.perinatologia.vo.ExamesPerinatologiaVO;
import br.gov.mec.aghu.perinatologia.vo.ResultadoExameSignificativoPerinatologiaVO;
import br.gov.mec.aghu.perinatologia.vo.UnidadeExamesSignificativoPerinatologiaVO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.locator.ServiceLocator;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.service.ServiceException;

/**
 * @author israel.haas
 */
@Stateless
public class RegistrarGestacaoRN extends BaseBusiness {

	private static final long serialVersionUID = -3423984755101821178L;

	@EJB
	private IPacienteService pacienteService;

	@Inject
	private McoGestacoesDAO mcoGestacoesDAO;

	@Inject
	private McoGestacoesJnDAO mcoGestacoesJnDAO;
	
	@Inject
	private McoResultadoExameSignifsDAO mcoResultadoExameSignifsDAO;

	@Inject
	@QualificadorUsuario
	private Usuario usuario;
	
	@EJB
	private IExamesService exameService;
	
	@Inject
	private McoExameExternoDAO mcoExameExternoDAO;
	
	@EJB
	private ICascaService cascaService;
	
	@EJB 
	private IEmergenciaFacade emergenciaFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
		
	private IPermissionService getPermissionService() {
		return ServiceLocator.getBeanRemote(IPermissionService.class, "aghu-casca");
	}
	
	@Override
	protected Log getLogger() {
		return null;
	}

	private enum RegistrarGestacaoRNExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_ULTIMA_MENSTRUACAO_MAIOR_DATAATUAL, 
		MENSAGEM_PRIMEIRA_ECO_MAIOR_DATAATUAL, 
		MENSAGEM_SERVICO_INDISPONIVEL,
		ERRO_DATA_HORA_OBRIGATORIA_AMNIOTOMIA,
		ERRO_DATA_HORA_IGNORADO_OBRIGATORIO_AMNIORREXIS, 
		MENSAGEM_ERRO_OBTER_PARAMETRO,
		MENSAGEM_USUARIO_NAO_LOGADO,
		ERRO_USUARIO_SEM_PERMISSAO_SOLICITACAO_EXAMES
		;
	}

	public void inserirMcoGestacoes(DadosGestacaoVO dadosGestacaoVO,
			DadosGestacaoVO dadosGestacaoVOOriginal, Integer pacCodigo)
			throws ApplicationBusinessException {

		McoGestacoes gestacao = this.preInserirMcoGestacoes(dadosGestacaoVO,
				dadosGestacaoVOOriginal, pacCodigo);

		McoGestacoesId id = new McoGestacoesId(pacCodigo,
				this.mcoGestacoesDAO.obterMaxSeqpMcoGestacoes(pacCodigo));
		gestacao.setId(id);

		gestacao.setGesta(dadosGestacaoVO.getGestacao());
		gestacao.setPara(dadosGestacaoVO.getParto());
		gestacao.setCesarea(dadosGestacaoVO.getCesariana());
		gestacao.setAborto(dadosGestacaoVO.getAborto());
		gestacao.setEctopica(dadosGestacaoVO.getEctopica());
		gestacao.setGravidez(dadosGestacaoVO.getGravidez());
		gestacao.setGemelar(dadosGestacaoVO.getGemelar());
		gestacao.setDtUltMenstruacao(dadosGestacaoVO.getDtUltimaMenstruacao());
		gestacao.setDtPrimEco(dadosGestacaoVO.getDtPrimeiraEco());
		gestacao.setIgPrimEco(dadosGestacaoVO.getEcoSemanas());
		gestacao.setIgPrimEcoDias(dadosGestacaoVO.getEcoDias());
		gestacao.setIgAtualSemanas(dadosGestacaoVO.getIgAtualSemanas());
		gestacao.setIgAtualDias(dadosGestacaoVO.getIgAtualDias());
		gestacao.setDtInformadaIg(dadosGestacaoVO.getDtInformadaIg());
		gestacao.setDtProvavelParto(dadosGestacaoVO.getDtProvavelParto());
		gestacao.setNumConsPrn(dadosGestacaoVO.getNroConsultas());
		gestacao.setDtPrimConsPrn(dadosGestacaoVO.getDtPrimeiraConsulta());
		gestacao.setVatCompleta(dadosGestacaoVO.getVatCompleta() != null ? dadosGestacaoVO.getVatCompleta() : false);
		gestacao.setIndMesmoPai(dadosGestacaoVO.getMesmoPai());
		gestacao.setGrupoSanguineoPai(dadosGestacaoVO.getTipoSanguineoPai());
		gestacao.setFatorRhPai(dadosGestacaoVO.getFatorRHPai());
		gestacao.setJustificativa(dadosGestacaoVO.getJustificativa());
		
		gestacao.setObsExames(dadosGestacaoVO.getObservacaoExame());

		this.mcoGestacoesDAO.persistir(gestacao);
		
		dadosGestacaoVOOriginal.setSeqp(gestacao.getId().getSeqp());
		dadosGestacaoVO.setSeqp(gestacao.getId().getSeqp());
		
	}

	/**
	 * @ORA_DBA TRIGGER “AGH”.MCOT_GSO_BRI
	 * @param dadosGestacaoVO
	 * @param dadosGestacaoVOOriginal
	 * @param pacCodigo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public McoGestacoes preInserirMcoGestacoes(DadosGestacaoVO dadosGestacaoVO,
			DadosGestacaoVO dadosGestacaoVOOriginal, Integer pacCodigo)
			throws ApplicationBusinessException {

		McoGestacoes gestacao = new McoGestacoes();
		gestacao.setCriadoEm(new Date());
		gestacao.setSerMatricula(usuario.getMatricula());
		gestacao.setSerVinCodigo(usuario.getVinculo());

		if (CoreUtil.modificados(dadosGestacaoVO.getDtUltimaMenstruacao(),
				dadosGestacaoVOOriginal.getDtUltimaMenstruacao())
				&& DateUtil.validaDataMaior(
						dadosGestacaoVO.getDtUltimaMenstruacao(), new Date())) {
			throw new ApplicationBusinessException(
					RegistrarGestacaoRNExceptionCode.MENSAGEM_ULTIMA_MENSTRUACAO_MAIOR_DATAATUAL);

		} else if (CoreUtil.modificados(dadosGestacaoVO.getDtPrimeiraEco(),
				dadosGestacaoVOOriginal.getDtPrimeiraEco())
				&& DateUtil.validaDataMaior(dadosGestacaoVO.getDtPrimeiraEco(),
						new Date())) {
			throw new ApplicationBusinessException(
					RegistrarGestacaoRNExceptionCode.MENSAGEM_PRIMEIRA_ECO_MAIOR_DATAATUAL);
		}
		return gestacao;
	}

	public void atualizarMcoGestacoes(DadosGestacaoVO dadosGestacaoVO,
			DadosGestacaoVO dadosGestacaoVOOriginal, Integer pacCodigo,
			Short seqp) throws ApplicationBusinessException {

		McoGestacoes gestacao = this.preAtualizarMcoGestacoes(dadosGestacaoVO,
				dadosGestacaoVOOriginal, pacCodigo, seqp);

		gestacao.setGesta(dadosGestacaoVO.getGestacao());
		gestacao.setPara(dadosGestacaoVO.getParto());
		gestacao.setCesarea(dadosGestacaoVO.getCesariana());
		gestacao.setAborto(dadosGestacaoVO.getAborto());
		gestacao.setEctopica(dadosGestacaoVO.getEctopica());
		gestacao.setGravidez(dadosGestacaoVO.getGravidez());
		gestacao.setGemelar(dadosGestacaoVO.getGemelar());
		gestacao.setDtUltMenstruacao(dadosGestacaoVO.getDtUltimaMenstruacao());
		gestacao.setDtPrimEco(dadosGestacaoVO.getDtPrimeiraEco());
		gestacao.setIgPrimEco(dadosGestacaoVO.getEcoSemanas());
		gestacao.setIgPrimEcoDias(dadosGestacaoVO.getEcoDias());
		gestacao.setIgAtualSemanas(dadosGestacaoVO.getIgAtualSemanas());
		gestacao.setIgAtualDias(dadosGestacaoVO.getIgAtualDias());
		gestacao.setDtInformadaIg(dadosGestacaoVO.getDtInformadaIg());
		gestacao.setDtProvavelParto(dadosGestacaoVO.getDtProvavelParto());
		gestacao.setDtPrimConsPrn(dadosGestacaoVO.getDtPrimeiraConsulta());
		gestacao.setNumConsPrn(dadosGestacaoVO.getNroConsultas());
		gestacao.setVatCompleta(dadosGestacaoVO.getVatCompleta());
		gestacao.setIndMesmoPai(dadosGestacaoVO.getMesmoPai());
		gestacao.setGrupoSanguineoPai(dadosGestacaoVO.getTipoSanguineoPai());
		gestacao.setFatorRhPai(dadosGestacaoVO.getFatorRHPai());
		gestacao.setJustificativa(dadosGestacaoVO.getJustificativa());
		
		gestacao.setObsExames(dadosGestacaoVO.getObservacaoExame());

		if (isMcoGestacoesAlterada(dadosGestacaoVO, dadosGestacaoVOOriginal)) {
			this.inserirJournalMcoGestacoes(dadosGestacaoVOOriginal,
					DominioOperacoesJournal.UPD);
		}

		this.mcoGestacoesDAO.atualizar(gestacao);
	}

	/**
	 * @ORA_DBA TRIGGER “AGH”.MCOT_GSO_BRU
	 * @param dadosGestacaoVO
	 * @param dadosGestacaoVOOriginal
	 */
	public McoGestacoes preAtualizarMcoGestacoes(
			DadosGestacaoVO dadosGestacaoVO,
			DadosGestacaoVO dadosGestacaoVOOriginal, Integer pacCodigo,
			Short seqp) throws ApplicationBusinessException {

		McoGestacoesId id = new McoGestacoesId(pacCodigo, seqp);
		McoGestacoes gestacao = this.mcoGestacoesDAO.obterPorChavePrimaria(id);

		if (CoreUtil.modificados(dadosGestacaoVO.getDtUltimaMenstruacao(),
				dadosGestacaoVOOriginal.getDtUltimaMenstruacao())
				&& DateUtil.validaDataMaior(
						dadosGestacaoVO.getDtUltimaMenstruacao(), new Date())) {
			throw new ApplicationBusinessException(
					RegistrarGestacaoRNExceptionCode.MENSAGEM_ULTIMA_MENSTRUACAO_MAIOR_DATAATUAL);

		} else if (CoreUtil.modificados(dadosGestacaoVO.getDtPrimeiraEco(),
				dadosGestacaoVOOriginal.getDtPrimeiraEco())
				&& DateUtil.validaDataMaior(dadosGestacaoVO.getDtPrimeiraEco(),
						new Date())) {
			throw new ApplicationBusinessException(
					RegistrarGestacaoRNExceptionCode.MENSAGEM_PRIMEIRA_ECO_MAIOR_DATAATUAL);
		}

		if (!CoreUtil.modificados(dadosGestacaoVO.getSerMatricula(),
				dadosGestacaoVOOriginal.getSerMatricula())
				&& !CoreUtil.modificados(dadosGestacaoVO.getSerVinCodigo(),
						dadosGestacaoVOOriginal.getSerVinCodigo())) {

			gestacao.setSerMatricula(usuario.getMatricula());
			gestacao.setSerVinCodigo(usuario.getVinculo());
		}
		return gestacao;
	}

	/**
	 * @ORA_DBA TRIGGER AGH.MCOT_GSO_ARU
	 * @param dadosGestacaoVOOriginal
	 * @param operacao
	 */
	public void inserirJournalMcoGestacoes(
			DadosGestacaoVO dadosGestacaoVOOriginal,
			DominioOperacoesJournal operacao) {

		McoGestacoesJn mcoGestacoesJn = new McoGestacoesJn();

		mcoGestacoesJn.setNomeUsuario(usuario.getLogin());
		mcoGestacoesJn.setOperacao(operacao);
		mcoGestacoesJn.setCriadoEm(dadosGestacaoVOOriginal.getCriadoEm());
		mcoGestacoesJn.setPacCodigo(dadosGestacaoVOOriginal.getPacCodigo());
		mcoGestacoesJn.setSeqp(dadosGestacaoVOOriginal.getSeqp());
		mcoGestacoesJn.setUsoMedicamentos(dadosGestacaoVOOriginal
				.getUsoMedicamentos());
		mcoGestacoesJn.setSerMatricula(dadosGestacaoVOOriginal
				.getSerMatricula());
		mcoGestacoesJn.setSerVinCodigo(dadosGestacaoVOOriginal
				.getSerVinCodigo());
		mcoGestacoesJn.setDthrSumarioAltaMae(dadosGestacaoVOOriginal
				.getDthrSumarioAltaMae());
		mcoGestacoesJn.setGesta(dadosGestacaoVOOriginal.getGestacao());
		mcoGestacoesJn.setPara(dadosGestacaoVOOriginal.getParto());
		mcoGestacoesJn.setCesarea(dadosGestacaoVOOriginal.getCesariana());
		mcoGestacoesJn.setAborto(dadosGestacaoVOOriginal.getAborto());
		mcoGestacoesJn.setEctopica(dadosGestacaoVOOriginal.getEctopica());
		mcoGestacoesJn.setGravidez(dadosGestacaoVOOriginal.getGravidez());
		mcoGestacoesJn.setGemelar(dadosGestacaoVOOriginal.getGemelar());
		mcoGestacoesJn.setDtUltMenstruacao(dadosGestacaoVOOriginal
				.getDtUltimaMenstruacao());
		mcoGestacoesJn.setDtPrimEco(dadosGestacaoVOOriginal.getDtPrimeiraEco());
		mcoGestacoesJn.setIgPrimEco(dadosGestacaoVOOriginal.getEcoSemanas());
		mcoGestacoesJn.setIgPrimEcoDias(dadosGestacaoVOOriginal.getEcoDias());
		mcoGestacoesJn.setIgAtualSemanas(dadosGestacaoVOOriginal
				.getIgAtualSemanas());
		mcoGestacoesJn.setIgAtualDias(dadosGestacaoVOOriginal.getIgAtualDias());
		mcoGestacoesJn.setDtInformadaIg(dadosGestacaoVOOriginal
				.getDtInformadaIg());
		mcoGestacoesJn.setDtProvavelParto(dadosGestacaoVOOriginal
				.getDtProvavelParto());
		mcoGestacoesJn.setNumConsPrn(dadosGestacaoVOOriginal.getNroConsultas());
		mcoGestacoesJn.setVatCompleta(dadosGestacaoVOOriginal.getVatCompleta());
		mcoGestacoesJn.setIndMesmoPai(dadosGestacaoVOOriginal.getMesmoPai());
		mcoGestacoesJn.setGrupoSanguineoPai(dadosGestacaoVOOriginal
				.getTipoSanguineoPai());
		mcoGestacoesJn.setFatorRhPai(dadosGestacaoVOOriginal.getFatorRHPai());

		this.mcoGestacoesJnDAO.persistir(mcoGestacoesJn);
	}

	public boolean isMcoGestacoesAlterada(DadosGestacaoVO dadosGestacaoVO,
			DadosGestacaoVO dadosGestacaoVOOriginal) {
		return CoreUtil.modificados(dadosGestacaoVO.getGestacao(),
				dadosGestacaoVOOriginal.getGestacao())
				|| CoreUtil.modificados(dadosGestacaoVO.getParto(),
						dadosGestacaoVOOriginal.getParto())
				|| CoreUtil.modificados(dadosGestacaoVO.getCesariana(),
						dadosGestacaoVOOriginal.getCesariana())
				|| CoreUtil.modificados(dadosGestacaoVO.getAborto(),
						dadosGestacaoVOOriginal.getAborto())
				|| CoreUtil.modificados(dadosGestacaoVO.getEctopica(),
						dadosGestacaoVOOriginal.getEctopica())
				|| CoreUtil.modificados(dadosGestacaoVO.getGravidez(),
						dadosGestacaoVOOriginal.getGravidez())
				|| CoreUtil.modificados(dadosGestacaoVO.getGemelar(),
						dadosGestacaoVOOriginal.getGemelar())
				|| CoreUtil.modificados(
						dadosGestacaoVO.getDtUltimaMenstruacao(),
						dadosGestacaoVOOriginal.getDtUltimaMenstruacao())
				|| CoreUtil.modificados(dadosGestacaoVO.getDtPrimeiraEco(),
						dadosGestacaoVOOriginal.getDtPrimeiraEco())
				|| CoreUtil.modificados(dadosGestacaoVO.getEcoSemanas(),
						dadosGestacaoVOOriginal.getEcoSemanas())
				|| CoreUtil.modificados(dadosGestacaoVO.getEcoDias(),
						dadosGestacaoVOOriginal.getEcoDias())
				|| CoreUtil.modificados(dadosGestacaoVO.getIgAtualSemanas(),
						dadosGestacaoVOOriginal.getIgAtualSemanas())
				|| CoreUtil.modificados(dadosGestacaoVO.getIgAtualDias(),
						dadosGestacaoVOOriginal.getIgAtualDias())
				|| CoreUtil.modificados(dadosGestacaoVO.getDtInformadaIg(),
						dadosGestacaoVOOriginal.getDtInformadaIg())
				|| CoreUtil.modificados(dadosGestacaoVO.getDtProvavelParto(),
						dadosGestacaoVOOriginal.getDtProvavelParto())
				|| CoreUtil.modificados(dadosGestacaoVO.getNroConsultas(),
						dadosGestacaoVOOriginal.getNroConsultas())
				|| CoreUtil.modificados(dadosGestacaoVO.getVatCompleta(),
						dadosGestacaoVOOriginal.getVatCompleta())
				|| CoreUtil.modificados(dadosGestacaoVO.getMesmoPai(),
						dadosGestacaoVOOriginal.getMesmoPai())
				|| CoreUtil.modificados(dadosGestacaoVO.getTipoSanguineoMae(),
						dadosGestacaoVOOriginal.getTipoSanguineoMae())
				|| CoreUtil.modificados(dadosGestacaoVO.getFatorRHMae(),
						dadosGestacaoVOOriginal.getFatorRHMae())
				|| CoreUtil.modificados(dadosGestacaoVO.getDtPrimeiraConsulta(),
						dadosGestacaoVOOriginal.getDtPrimeiraConsulta())
				|| CoreUtil.modificados(dadosGestacaoVO.getTipoSanguineoPai(),
						dadosGestacaoVOOriginal.getTipoSanguineoPai())
				|| CoreUtil.modificados(dadosGestacaoVO.getFatorRHPai(),
						dadosGestacaoVOOriginal.getFatorRHPai())
				|| CoreUtil.modificados(dadosGestacaoVO.getJustificativa(),
						dadosGestacaoVOOriginal.getJustificativa())
				|| CoreUtil.modificados(dadosGestacaoVO.getObservacaoExame(),
						dadosGestacaoVOOriginal.getObservacaoExame());
	}

	public void atualizarRegSanguineo(Integer pacCodigo, Short seqp,
			DadosGestacaoVO dadosGestacaoVO)
			throws ApplicationBusinessException {
		try {
			this.pacienteService.atualizarRegSanguineo(pacCodigo,
					seqp.byteValue(), dadosGestacaoVO.getTipoSanguineoMae(),
					dadosGestacaoVO.getFatorRHMae(),
					dadosGestacaoVO.getCoombs());
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(
					RegistrarGestacaoRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(
					RegistrarGestacaoRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
	}

	public void inserirRegSanguineo(Integer pacCodigo,
			DadosGestacaoVO dadosGestacaoVO)
			throws ApplicationBusinessException {
		try {
			this.pacienteService.inserirRegSanguineo(pacCodigo,
					dadosGestacaoVO.getTipoSanguineoMae(),
					dadosGestacaoVO.getFatorRHMae(),
					dadosGestacaoVO.getCoombs(), usuario.getMatricula(),
					usuario.getVinculo());
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(
					RegistrarGestacaoRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(
					RegistrarGestacaoRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
	}
	
	/**
	 * Suggestion Exames - COUNT
	 * 
	 * C2 de #25644 - INFORMAR CARGA EXAME
	 * 
	 * @param param
	 * @return
	 */
	public Long pesquisarExamesCount(String param) throws ApplicationBusinessException {
		Long result = 0l; 
		List<ExameSignificativoVO> examesSignificativos = this.pesquisarExamesSignificativosPrinato(null);
		List<ExamesPerinatologiaVO> examesPerinato = this.pesquisarExamesPerinato(param);
		//RESOLVENDO O JOIN
		for (ExamesPerinatologiaVO examesPerinatologiaVO : examesPerinato) {
			ExameSignificativoVO exameSignificativoVO = this.obterExameSignificativoVO(examesSignificativos,
					examesPerinatologiaVO.getEmaManSeq(), examesPerinatologiaVO.getEmaExaSigla());
			if (exameSignificativoVO != null) {
				result++;
			}
		}
		return result;
	}
	
	/**
	 * Suggestion Exames - PESQUISA
	 * 
	 * C2 de #25644 - INFORMAR CARGA EXAME
	 * 
	 * @param param
	 * @return
	 */
	public List<UnidadeExamesSignificativoPerinatologiaVO> pesquisarExames(String param) throws ApplicationBusinessException {
		return this.pesquisarExames(param, null, 100);
	}
	
	/**
	 * Consulta utilizada para obter os dados de exames que são default para uma gestação
	 * 
	 * C1 de #25644 - INFORMAR CARGA EXAME
	 * 
	 * @param param
	 * @return
	 */
	public List<UnidadeExamesSignificativoPerinatologiaVO> pesquisarExamesDefault() throws ApplicationBusinessException {
		return this.pesquisarExames(null, Boolean.TRUE, null);
	}
	
	private List<UnidadeExamesSignificativoPerinatologiaVO> pesquisarExames(String param, Boolean indCargaExame, Integer maxResult) throws ApplicationBusinessException {
		List<UnidadeExamesSignificativoPerinatologiaVO> result = new ArrayList<UnidadeExamesSignificativoPerinatologiaVO>(); 
		List<ExameSignificativoVO> examesSignificativos = this.pesquisarExamesSignificativosPrinato(indCargaExame);
		List<ExamesPerinatologiaVO> examesPerinato = this.pesquisarExamesPerinato(param);
		
		//RESOLVENDO O JOIN
		for (ExamesPerinatologiaVO examesPerinatologiaVO : examesPerinato) {
			ExameSignificativoVO exameSignificativoVO = this.obterExameSignificativoVO(examesSignificativos,
					examesPerinatologiaVO.getEmaManSeq(), examesPerinatologiaVO.getEmaExaSigla());
			if (exameSignificativoVO != null) {
				
				UnidadeExamesSignificativoPerinatologiaVO exameResult = new UnidadeExamesSignificativoPerinatologiaVO();
				
				exameResult.setEmaExaSigla(exameSignificativoVO.getEmaExaSigla());
				exameResult.setEmaManSeq(exameSignificativoVO.getEmaManSeq());
				exameResult.setUnfSeq(exameSignificativoVO.getUnfSeq());
				exameResult.setEexSeq(examesPerinatologiaVO.getEexSeq());
				exameResult.setDescricao(examesPerinatologiaVO.getDescricao());
				
				result.add(exameResult);
			}
		}

		//RESOLVENDO ORDENAÇÃO
		Collections.sort(result, new Comparator<UnidadeExamesSignificativoPerinatologiaVO>() {
			@Override
			public int compare(UnidadeExamesSignificativoPerinatologiaVO vo1, UnidadeExamesSignificativoPerinatologiaVO vo2) {
				String desc1 = vo1 != null ? vo1.getDescricao() : StringUtils.EMPTY;
				String desc2 = vo2 != null ? vo2.getDescricao() : StringUtils.EMPTY;
				final Collator collator = Collator.getInstance(new Locale("pt","BR"));
				collator.setStrength(Collator.TERTIARY);
				return collator.compare(desc1, desc2);
			}
		});
		
		//LIMITANDO EM 100 REGISTROS
		if(maxResult != null && result.size() > maxResult.intValue()){
			result = result.subList(0, maxResult.intValue());
		}
		
		return result;
	}
	
	/**
	 * Procura na lista examesSignificativos um elemento através de emaManSeq e emaExaSigla
	 * @param examesSignificativos
	 * @param emaManSeq
	 * @param emaExaSigla
	 * @return
	 */
	private ExameSignificativoVO obterExameSignificativoVO(List<ExameSignificativoVO> examesSignificativos, Integer emaManSeq,
			String emaExaSigla) {
		for (ExameSignificativoVO exameSignificativoVO : examesSignificativos) {
			if (CoreUtil.igual(exameSignificativoVO.getEmaManSeq(), emaManSeq)
					&& CoreUtil.igual(exameSignificativoVO.getEmaExaSigla(), emaExaSigla)) {
				return exameSignificativoVO;
			}
		}
		return null;
	}
	
	private List<ExameSignificativoVO> pesquisarExamesSignificativosPrinato(Boolean indCargaExame) throws ApplicationBusinessException {
		
		Object parametro = null;
		String nomeParametro = "P_UNIDADE_CO";
		Short unidadeCO = null;

		parametro = parametroFacade.obterAghParametroPorNome(nomeParametro, "vlrNumerico");
		
		if (parametro != null) {
			unidadeCO = ((BigDecimal) parametro).shortValue();
		}
		
		List<ExameSignificativoVO> examesSignificativos = null;
		try {
			examesSignificativos = exameService.pesquisarAelUnidExameSignificativoPorUnfSeq(unidadeCO, indCargaExame);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(RegistrarGestacaoRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		
		return examesSignificativos;
	}
	
	/**
	 * C5 de #25644 - INFORMAR CARGA EXAME
	 * 
	 * @param param
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<ExamesPerinatologiaVO> pesquisarExamesPerinato(String param) throws ApplicationBusinessException {

		List<ExamesPerinatologiaVO> result = new ArrayList<ExamesPerinatologiaVO>();

		List<ExameMaterialAnaliseVO> examesMaterialAnalise = this.obterExamesMaterialAnalise(param);

		List<McoExameExterno> examesExternos = mcoExameExternoDAO.pesquisarMcoExameExternoAtivosPorDescricao(param);

		// RESOLVENDO A UNION DE C5 DE #25644
		for (ExameMaterialAnaliseVO exameMaterialAnaliseVO : examesMaterialAnalise) {
			ExamesPerinatologiaVO examesPerinatologiaVO = this.obterExamesPerinatologiaVOInterno(exameMaterialAnaliseVO);
			result.add(examesPerinatologiaVO);
		}

		for (McoExameExterno mcoExameExterno : examesExternos) {
			ExamesPerinatologiaVO examesPerinatologiaVO = this.obterExamesPerinatologiaVOExterno(mcoExameExterno);
			result.add(examesPerinatologiaVO);
		}

		return result;
	}

	private List<ExameMaterialAnaliseVO> obterExamesMaterialAnalise(String param) throws ApplicationBusinessException {
		List<ExameMaterialAnaliseVO> examesMaterialAnalise = null;
		try {
			examesMaterialAnalise = exameService.pesquisarAtivosPorSiglaOuDescricao(param, null);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RegistrarGestacaoRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return examesMaterialAnalise;
	}

	private ExamesPerinatologiaVO obterExamesPerinatologiaVOInterno(ExameMaterialAnaliseVO exameMaterialAnaliseVO) {
		ExamesPerinatologiaVO examesPerinatologiaVO = new ExamesPerinatologiaVO();

		StringBuffer chave = new StringBuffer();
		chave = chave.append(StringUtils.isNotBlank(exameMaterialAnaliseVO.getSigla()) ? exameMaterialAnaliseVO.getSigla() : " "); // NVL(EMA.EXA_SIGLA,' ')||
		chave = chave.append(exameMaterialAnaliseVO.getMamSeq() != null ? exameMaterialAnaliseVO.getMamSeq() : '0'); // NVL(TO_CHAR(EMA.MAN_SEQ),'0')||
		chave.append('0');// '0'
		examesPerinatologiaVO.setChave(chave.toString()); // NVL(EMA.EXA_SIGLA,' ')||NVL(TO_CHAR(EMA.MAN_SEQ),'0')||'0' CHAVE,

		examesPerinatologiaVO.setEmaExaSigla(exameMaterialAnaliseVO.getSigla()); // EMA.EXA_SIGLA EMA_EXA_SIGLA,
		examesPerinatologiaVO.setEmaManSeq(exameMaterialAnaliseVO.getMamSeq()); // EMA.MAN_SEQ EMA_MAN_SEQ,
		examesPerinatologiaVO.setEexSeq(null); // TO_NUMBER(NULL) EEX_SEQ,

		StringBuffer descricao = new StringBuffer();
		descricao = descricao.append(StringUtils.isNotBlank(exameMaterialAnaliseVO.getDescricao()) ? exameMaterialAnaliseVO.getDescricao() : StringUtils.EMPTY);
		descricao = descricao.append('-');
		descricao.append(StringUtils.isNotBlank(exameMaterialAnaliseVO.getMaterial()) ? exameMaterialAnaliseVO.getMaterial() : StringUtils.EMPTY);
		examesPerinatologiaVO.setDescricao(descricao.toString()); // EXA.DESCRICAO_USUAL||'-'||MAN.DESCRICAO DESCRICAO,

		examesPerinatologiaVO.setOrigem("HCPA"); // 'HCPA' ORIGEM
		return examesPerinatologiaVO;
	}

	private ExamesPerinatologiaVO obterExamesPerinatologiaVOExterno(McoExameExterno mcoExameExterno) {
		ExamesPerinatologiaVO examesPerinatologiaVO = new ExamesPerinatologiaVO();

		StringBuffer chave = new StringBuffer();
		chave = chave.append(' '); // ' '||
		chave = chave.append('0'); // '0'||
		chave.append(mcoExameExterno.getSeq() != null ? mcoExameExterno.getSeq() : '0'); // NVL(TO_CHAR(EEX.SEQ),'0')
		examesPerinatologiaVO.setChave(chave.toString()); // ' '||'0'||NVL(TO_CHAR(EEX.SEQ),'0') CHAVE,

		examesPerinatologiaVO.setEmaExaSigla(null); // NULL EMA_EXA_SIGLA,
		examesPerinatologiaVO.setEmaManSeq(null); // TO_NUMBER(NULL) EMA_MAN_SEQ,
		examesPerinatologiaVO.setEexSeq(mcoExameExterno.getSeq()); // EEX.SEQ EEX_SEQ,

		examesPerinatologiaVO.setDescricao(mcoExameExterno.getDescricao()); // EEX.DESCRICAO DESCRICAO,

		examesPerinatologiaVO.setOrigem("EXT"); // 'EXT' ORIGEM
		return examesPerinatologiaVO;
	}

	/**
	 * C3 de #25644 - INFORMAR CARGA EXAME
	 * 
	 * @param gsoPacCodigo
	 * @param gsoSeqp
	 * @return
	 */
	public List<ResultadoExameSignificativoPerinatologiaVO> pesquisarMcoResultadoExamePerinatologiaPorGSO(Integer gsoPacCodigo,
			Short gsoSeqp) throws ApplicationBusinessException {
		List<ResultadoExameSignificativoPerinatologiaVO> result = new ArrayList<ResultadoExameSignificativoPerinatologiaVO>();
		List<McoResultadoExameSignifs> listMcoResultadoExameSignifs = mcoResultadoExameSignifsDAO.pesquisarMcoResultadoExameSignifsPorGSO(
				gsoPacCodigo, gsoSeqp);
		if (listMcoResultadoExameSignifs != null && !listMcoResultadoExameSignifs.isEmpty()) {
			for (McoResultadoExameSignifs mcoResultadoExameSignifs : listMcoResultadoExameSignifs) {
				ResultadoExameSignificativoPerinatologiaVO resultadoExameSignificativoPerinatologiaVO = new ResultadoExameSignificativoPerinatologiaVO();
				// RES.SEQP,
				resultadoExameSignificativoPerinatologiaVO.setSeqp(mcoResultadoExameSignifs.getId().getSeqp());
				// RES.DATA_REALIZACAO,
				resultadoExameSignificativoPerinatologiaVO.setDataRealizacao(mcoResultadoExameSignifs.getDataRealizacao());
				// RES.RESULTADO,
				resultadoExameSignificativoPerinatologiaVO.setResultado(mcoResultadoExameSignifs.getResultado());
				// RES.GSO_PAC_CODIGO,
				resultadoExameSignificativoPerinatologiaVO.setGsoPacCodigo(mcoResultadoExameSignifs.getId().getGsoPacCodigo());
				// RES.GSO_SEQP,
				resultadoExameSignificativoPerinatologiaVO.setGsoSeqp(mcoResultadoExameSignifs.getId().getGsoSeqp());
				// RES.ISE_SOE_SEQ,
				resultadoExameSignificativoPerinatologiaVO.setIseSoeSeq(mcoResultadoExameSignifs.getIseSoeSeq());
				// RES.ISE_SEQP,
				resultadoExameSignificativoPerinatologiaVO.setIseSeqp(mcoResultadoExameSignifs.getIseSeqp());
				// RES.EMA_EXA_SIGLA,
				resultadoExameSignificativoPerinatologiaVO.setEmaExaSigla(mcoResultadoExameSignifs.getEmaExaSigla());
				// RES.EMA_MAN_SEQ,
				resultadoExameSignificativoPerinatologiaVO.setEmaManSeq(mcoResultadoExameSignifs.getEmaManSeq());

				if (mcoResultadoExameSignifs.getExameExterno() != null) {
					// MEE.DESCRICAO DESCRICAO_EXAME_EXTERNO,
					resultadoExameSignificativoPerinatologiaVO.setDescricaoExameExterno(mcoResultadoExameSignifs.getExameExterno()
							.getDescricao());
					// RES.EEX_SEQ
					resultadoExameSignificativoPerinatologiaVO.setEexSeq(mcoResultadoExameSignifs.getExameExterno().getSeq());
				}

				if(mcoResultadoExameSignifs.getEmaExaSigla() != null && mcoResultadoExameSignifs.getEmaManSeq() != null){
					List<DadosExameMaterialAnaliseVO> listDadosExameMaterialAnaliseVO = null;
					try {
						listDadosExameMaterialAnaliseVO = exameService.pesquisarExamesPorSiglaMaterialAnalise(
								mcoResultadoExameSignifs.getEmaExaSigla(), mcoResultadoExameSignifs.getEmaManSeq());
					} catch (ServiceException e) {
						throw new ApplicationBusinessException(RegistrarGestacaoRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
					} catch (RuntimeException e) {
						throw new ApplicationBusinessException(RegistrarGestacaoRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
					}
					if (listDadosExameMaterialAnaliseVO != null && !listDadosExameMaterialAnaliseVO.isEmpty()) {
						DadosExameMaterialAnaliseVO dadosExameMaterialAnaliseVO = listDadosExameMaterialAnaliseVO.get(0);
						// EXA.DESCRICAO_USUAL||'-'||MAN.DESCRICAO DESCRICAO, --Serviço #37700
						resultadoExameSignificativoPerinatologiaVO.setDescricao(dadosExameMaterialAnaliseVO.getDescricao());
						// EXA.SIGLA --Serviço #37700
						resultadoExameSignificativoPerinatologiaVO.setSigla(dadosExameMaterialAnaliseVO.getSigla());
					}
				}

				result.add(resultadoExameSignificativoPerinatologiaVO);
			}
		}
		return result;
	}

	/**
	 * RN01 de #25644 - INFORMAR CARGA EXAME
	 * 
	 * @param gsoPacCodigo
	 * @param gsoSeqp
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Object[] carregarExames(Integer gsoPacCodigo, Short gsoSeqp) throws ApplicationBusinessException {

		List<UnidadeExamesSignificativoPerinatologiaVO> unidadeExamesSignificativoPerinatologiaVOList = null;

		List<ExameResultados> examesResultados = new ArrayList<ExameResultados>();
		List<Date> datasExames = new ArrayList<Date>();

		// 1. Executar consulta C3
		List<ResultadoExameSignificativoPerinatologiaVO> resultadoExameSignificativoPerinatologiaVOList = this.pesquisarMcoResultadoExamePerinatologiaPorGSO(gsoPacCodigo, gsoSeqp);

		// 2. Se retornou resultados montar o grid de acordo com o protótipo da imagem 1
		if (resultadoExameSignificativoPerinatologiaVOList != null && !resultadoExameSignificativoPerinatologiaVOList.isEmpty()) {

			// Monta lista de exames e lista de datas de realização
			this.montarListaExamesDatasRealizacao(resultadoExameSignificativoPerinatologiaVOList, examesResultados, datasExames);
			
			// Ordena as data
			this.ordenarListaDatasExames(datasExames);
			
			// Ordena os exames
			this.ordenarExames(examesResultados);
						
			// PARA CADA EXAME
			for (ExameResultados exameResultado : examesResultados) {
				this.atribuirResultados(gsoPacCodigo, gsoSeqp, datasExames, resultadoExameSignificativoPerinatologiaVOList, exameResultado);
			}
		}
		// 3. Se não retornou resultados executar a consulta C1 para obter a carga de exames default
		else {
			unidadeExamesSignificativoPerinatologiaVOList = this.pesquisarExamesDefault();
			for (UnidadeExamesSignificativoPerinatologiaVO unidadeExamesSignificativoPerinatologiaVO : unidadeExamesSignificativoPerinatologiaVOList) {
				examesResultados.add(new ExameResultados(unidadeExamesSignificativoPerinatologiaVO));
			}
		}
		
		return new Object[] { examesResultados, datasExames };
	}

	private void atribuirResultados(Integer gsoPacCodigo, Short gsoSeqp, List<Date> datasExames,
			List<ResultadoExameSignificativoPerinatologiaVO> resultadoExameSignificativoPerinatologiaVOList, ExameResultados exameResultado) {
		UnidadeExamesSignificativoPerinatologiaVO exame = exameResultado.getExame();
		//INICIALIZA ARRAY DE RESULTADOS
		ResultadoExameSignificativoPerinatologiaVO[] resultados = new ResultadoExameSignificativoPerinatologiaVO[datasExames.size()];
		// PARA CADA DATA DE REALIZAÇÃO
		int indiceResultado = 0;
		for (Date dataRealizacao : datasExames) {
			this.popularResultadoExame(gsoPacCodigo, gsoSeqp, resultadoExameSignificativoPerinatologiaVOList, exame, resultados, indiceResultado, dataRealizacao);
			indiceResultado++;
		}
		exameResultado.setResultados(resultados);
	}

	private void popularResultadoExame(Integer gsoPacCodigo, Short gsoSeqp,
			List<ResultadoExameSignificativoPerinatologiaVO> resultadoExameSignificativoPerinatologiaVOList,
			UnidadeExamesSignificativoPerinatologiaVO exame, ResultadoExameSignificativoPerinatologiaVO[] resultados, int indiceResultado,
			Date dataRealizacao) {
		// PROCURA O RESULTADO CORRESPONDENTE
		boolean existe = false;
		for (ResultadoExameSignificativoPerinatologiaVO resultadoExameSignificativoPerinatologiaVO : resultadoExameSignificativoPerinatologiaVOList) {
			if (DateUtil.isDatasIguais(resultadoExameSignificativoPerinatologiaVO.getDataRealizacao(), dataRealizacao)) {
				UnidadeExamesSignificativoPerinatologiaVO exam = this.obterExame(resultadoExameSignificativoPerinatologiaVO);
				if (exam.equals(exame)) {
					resultados[indiceResultado] = resultadoExameSignificativoPerinatologiaVO;
					existe = true;
					break;
				}
			}
		}
		if (!existe) {
			resultados[indiceResultado] = new ResultadoExameSignificativoPerinatologiaVO(gsoPacCodigo, gsoSeqp, null, null,
					exame.getEmaExaSigla(), exame.getEmaManSeq(), exame.getEexSeq(), exame.getDescricao());
		}		
	}

	private void ordenarExames(List<ExameResultados> examesResultados) {
		// Ordena os exames
		Collections.sort(examesResultados, new Comparator<ExameResultados>() {
			@Override
			public int compare(ExameResultados vo1, ExameResultados vo2) {
				String desc1 = vo1.getExame().getDescricao() != null ? vo1.getExame().getDescricao() : StringUtils.EMPTY;
				String desc2 = vo2.getExame().getDescricao() != null ? vo2.getExame().getDescricao() : StringUtils.EMPTY;
				return desc1.compareTo(desc2);
			}
		});
	}

	private void ordenarListaDatasExames(List<Date> datasExames) {
		// Ordena as data
		Collections.sort(datasExames, new Comparator<Date>() {
			@Override
			public int compare(Date vo1, Date vo2) {
				if(vo1 == null && vo2 == null){
					return 0;
				} else if(vo1 != null && vo2 == null){
					return 1;
				} else if(vo1 == null && vo2 != null){
					return -1;
				}
				return vo1.compareTo(vo2);
			}
		});
	}

	private void montarListaExamesDatasRealizacao(List<ResultadoExameSignificativoPerinatologiaVO> resultadoExameSignificativoPerinatologiaVOList, List<ExameResultados> examesResultados, List<Date> datasExames) {
		// Monta lista de exames e lista de datas de realização
		
		List<UnidadeExamesSignificativoPerinatologiaVO> unidadeExamesSignificativoPerinatologiaVOList = new ArrayList<UnidadeExamesSignificativoPerinatologiaVO>();
		
		for (ResultadoExameSignificativoPerinatologiaVO resultado : resultadoExameSignificativoPerinatologiaVOList) {
			
			UnidadeExamesSignificativoPerinatologiaVO exame = this.obterExame(resultado);
			
			if (!unidadeExamesSignificativoPerinatologiaVOList.contains(exame)) {
				unidadeExamesSignificativoPerinatologiaVOList.add(exame);
				examesResultados.add(new ExameResultados(exame));
			}
			
			Date dataRealizacao = resultado.getDataRealizacao();
			if (dataRealizacao != null && !datasExames.contains(dataRealizacao)) {
				datasExames.add(dataRealizacao);
			}
		}
	}
	
	/**
	 * Monta um Exame à partir do Resultado.
	 * @return
	 */
	private UnidadeExamesSignificativoPerinatologiaVO obterExame(ResultadoExameSignificativoPerinatologiaVO resultado){
		UnidadeExamesSignificativoPerinatologiaVO exame = new UnidadeExamesSignificativoPerinatologiaVO();
		exame.setEmaExaSigla(resultado.getEmaExaSigla());
		exame.setEmaManSeq(resultado.getEmaManSeq());
		exame.setUnfSeq(null);
		exame.setEexSeq(resultado.getEexSeq());
		if (resultado.getEexSeq() != null) {
			exame.setDescricao(resultado.getDescricaoExameExterno());
		} else {
			exame.setDescricao(resultado.getDescricao());
		}
		return exame;
	}

	public boolean verificarSeModuloEstaAtivo(String nome) throws ApplicationBusinessException {
		try {
			return cascaService.verificarSeModuloEstaAtivo(nome);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(RegistrarGestacaoRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}		
	}
	
	public void verificarPermissaoParaSolicitarExames(Integer matricula, Short vinCodigo) throws ApplicationBusinessException {
		try {
			//Verifica Permissão para Imprimir Relatórios Definitivos
			boolean permPermissaoImpRelatDefinitivos = emergenciaFacade.verificarPermissaoUsuarioImprimirRelatorioDefinitivo(matricula, vinCodigo);
			
			verificarPermissaoUsuarioLogado(permPermissaoImpRelatDefinitivos);			
			
		} catch (BaseRuntimeException e) {		
			throw new ApplicationBusinessException(RegistrarGestacaoRNExceptionCode.MENSAGEM_USUARIO_NAO_LOGADO, e.getMessage());
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RegistrarGestacaoRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}	
	}

	private void verificarPermissaoUsuarioLogado(boolean permPermissaoImpRelatDefinitivos) throws ApplicationBusinessException {
		String nomeUsuarioLogado = obterLoginUsuarioLogado();			
		boolean permElaborarSolicitacaoExame = getPermissionService().usuarioTemPermissao(nomeUsuarioLogado, "elaborarSolicitacaoExame", "elaborar");
		boolean permsolicitarExamesLoteAssinar = getPermissionService().usuarioTemPermissao(nomeUsuarioLogado, "solicitarExamesLoteAssinar", "elaborar");
		if(!permPermissaoImpRelatDefinitivos || !permElaborarSolicitacaoExame || !permsolicitarExamesLoteAssinar){
			throw new ApplicationBusinessException(RegistrarGestacaoRNExceptionCode.ERRO_USUARIO_SEM_PERMISSAO_SOLICITACAO_EXAMES);
		}
	}	
	
	//Executa Serviço #37234
	public Integer obterAtendimentoSeqPorNumeroDaConsulta(Integer numeroConsulta) {
		return ambulatorioFacade.obterAtendimentoPorConNumero(numeroConsulta);	
	}
}