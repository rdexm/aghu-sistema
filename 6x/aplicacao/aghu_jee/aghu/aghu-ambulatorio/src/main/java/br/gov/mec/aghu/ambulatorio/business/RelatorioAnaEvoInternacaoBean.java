package br.gov.mec.aghu.ambulatorio.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamRegistroDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTipoItemAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTipoItemEvolucaoDAO;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.CseCategoriaProfissional;
import br.gov.mec.aghu.model.MamAnamneses;
import br.gov.mec.aghu.model.MamEvolucoes;
import br.gov.mec.aghu.model.MamRegistro;
import br.gov.mec.aghu.model.MamTipoItemAnamneses;
import br.gov.mec.aghu.model.MamTipoItemEvolucao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.vo.RelatorioAnaEvoInternacaoVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;


@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@SuppressWarnings("PMD.AghuClassesOnRnPublicas")
public class RelatorioAnaEvoInternacaoBean extends BaseBMTBusiness implements RelatorioAnaEvoInternacaoBeanLocal {
	
	private static final Log LOG = LogFactory.getLog(RelatorioAnaEvoInternacaoBean.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;
	
	@Inject
	private MamTipoItemEvolucaoDAO mamTipoItemEvolucaoDAO;
	
	@Inject
	private MamEvolucoesDAO mamEvolucoesDAO;
	
	@Inject
	private MamAnamnesesDAO mamAnamnesesDAO;
	
	@Inject
	private MamTipoItemAnamnesesDAO mamTipoItemAnamnesesDAO;
	
	@Inject
	private AacConsultasDAO aacConsultasDAO;
	
	@Inject
	private MamRegistroDAO mamRegistroDAO;
	
	@EJB
	private AtendimentoPacientesAgendadosRN atendimentoPacientesAgendadosRN;
	
	@EJB
	private RelatorioAnaEvoInternacaoRN relatorioAnaEvoInternacaoRN;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	private static final long serialVersionUID = 2660450871099576340L;
	
	private static final int TEMPO_12_HORAS = (60 * 60 * 12); // 12 horas

	private enum EnumTargetRelatorioAnaEvoInternacao {
		RELATORIO_EVOLUCAO_TODOS, RELATORIO_EVOLUCAO_PERIODO, RELATORIO_ANAMNESES;
	}

	private enum RelatorioAnaEvoInternacaoBeanExceptionCode implements
			BusinessExceptionCode {
		RELATORIO_EVOLUCAO_PERIODO_MAIOR_30_DIAS
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	// @TransactionTimeout(60 * 60 * 12) // 12 horas
	public List<RelatorioAnaEvoInternacaoVO> pesquisarRelatorioAnaEvoInternacao(Integer atdSeq, String tipoRelatorio, Date dataInicial, Date dataFinal) throws ApplicationBusinessException {
		try {
			beginTransaction(TEMPO_12_HORAS);
			
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			CseCategoriaProfissional categoriaProfissional = getCascaFacade().primeiraCategoriaProfissional(servidorLogado);
			
			List<RelatorioAnaEvoInternacaoVO> result = new ArrayList<RelatorioAnaEvoInternacaoVO>();
			if (EnumTargetRelatorioAnaEvoInternacao.RELATORIO_ANAMNESES.toString().equals(tipoRelatorio)) {
				result = pesquisarRelatorioAnamneses(atdSeq);
			} else if (EnumTargetRelatorioAnaEvoInternacao.RELATORIO_EVOLUCAO_TODOS.toString().equals(tipoRelatorio)) {
				result = pesquisarRelatorioEvolucao(atdSeq, categoriaProfissional);
			} else if (EnumTargetRelatorioAnaEvoInternacao.RELATORIO_EVOLUCAO_PERIODO.toString().equals(tipoRelatorio)) {
				result = pesquisarRelatorioEvolucaoPeriodo(atdSeq, DateUtil.truncaData(dataInicial), DateUtil.truncaDataFim(dataFinal), categoriaProfissional);
			}
					
			commitTransaction();
			
			return result;
		} catch (ApplicationBusinessException e) {
			rollbackTransaction();
			throw e;
		}
	}
	
	private List<RelatorioAnaEvoInternacaoVO> pesquisarRelatorioAnamneses(Integer atdSeq) throws ApplicationBusinessException {
		AghParametros parametroMed = parametroFacade.obterAghParametro(AghuParametrosEnum.P_GRUPO_PROFISSIONAL_ANAMNESE_MED);
		AghParametros parametroEnf = parametroFacade.obterAghParametro(AghuParametrosEnum.P_GRUPO_PROFISSIONAL_ANAMNESE_ENF);
		AghParametros parametroNut = parametroFacade.obterAghParametro(AghuParametrosEnum.P_GRUPO_PROFISSIONAL_ANAMNESE_NUT);
		
		List<RelatorioAnaEvoInternacaoVO> result = new ArrayList<RelatorioAnaEvoInternacaoVO>();
		
		result = executaComplementarDadosComTipoItemAnamnese(getMamAnamnesesDAO().pesquisarAnamnesesPorAtendimentoRelatorioAnaEvoInternacao(atdSeq));
		for (RelatorioAnaEvoInternacaoVO vo : result) {
			executaComplementarDadosRelatorioAnamneses(vo, parametroMed, parametroEnf, parametroNut);
		}
		
		if (!result.isEmpty()) {
			executaComplementarRodapeRelatorioAnaEvoInternacao(result.get(0), Boolean.FALSE);
		}
		
		return result;
	}

	@Override
	public List<RelatorioAnaEvoInternacaoVO> complementarDadosComTipoItemAnamnese(List<RelatorioAnaEvoInternacaoVO> list) {
		try{
			beginTransaction(TEMPO_12_HORAS);
			return executaComplementarDadosComTipoItemAnamnese(list);
		} finally {
			commitTransaction();
		}
	}

	private List<RelatorioAnaEvoInternacaoVO> executaComplementarDadosComTipoItemAnamnese(List<RelatorioAnaEvoInternacaoVO> list) {
		List<MamTipoItemAnamneses> tipoItems = getMamTipoItemAnamnesesDAO().pesquisarTipoItemAnamneseOrdenado();
		List<RelatorioAnaEvoInternacaoVO> retorno = new ArrayList<RelatorioAnaEvoInternacaoVO>();
		for (RelatorioAnaEvoInternacaoVO relatorioAnaEvoInternacaoVO : list) {
			for (MamTipoItemAnamneses tipoItem : tipoItems) {
				RelatorioAnaEvoInternacaoVO vo = new RelatorioAnaEvoInternacaoVO();
				vo.setDthrValidaAna(relatorioAnaEvoInternacaoVO.getDthrValidaAna());
				vo.setRgtSeq(relatorioAnaEvoInternacaoVO.getRgtSeq());
				vo.setCodPac(relatorioAnaEvoInternacaoVO.getCodPac());	
				vo.setTinDescricao(tipoItem.getDescricao());
				vo.setTinOrdem(tipoItem.getOrdem());
				vo.setTinSeq(tipoItem.getSeq());
				vo.setAnaSeq(relatorioAnaEvoInternacaoVO.getAnaSeq());
				retorno.add(vo);
			}
		}
		return retorno;
	}
	
	@Override
	public List<RelatorioAnaEvoInternacaoVO> complementarDadosComTipoItemEvolucao(List<RelatorioAnaEvoInternacaoVO> list) throws ApplicationBusinessException {
		try{
			beginTransaction(TEMPO_12_HORAS);
			final List<RelatorioAnaEvoInternacaoVO> result = executaComplementarDadosComTipoItemEvolucao(list);
			commitTransaction();
			return result;
		} catch (ApplicationBusinessException e) {
			rollbackTransaction();
			throw e;
		}
	}

	private List<RelatorioAnaEvoInternacaoVO> executaComplementarDadosComTipoItemEvolucao(
			List<RelatorioAnaEvoInternacaoVO> list)
			throws ApplicationBusinessException {
		LOG.debug("list size: " + list.size());
		List<MamTipoItemEvolucao> tipoItems = mamTipoItemEvolucaoDAO.pesquisarTipoItemEvolucaoOrdenado();
		LOG.debug("tipoItems size: " + tipoItems.size());
		
		List<RelatorioAnaEvoInternacaoVO> retorno = new ArrayList<RelatorioAnaEvoInternacaoVO>();
		for (RelatorioAnaEvoInternacaoVO relatorioAnaEvoInternacaoVO : list) {
			String responsavel = obterIdentidadeResponsavel(relatorioAnaEvoInternacaoVO.getEvoSeq(), null);			
			String visto = prontuarioOnlineFacade.visualizarNotaEvolucaoEMG(relatorioAnaEvoInternacaoVO.getRgtSeq(), null);
			
			for (MamTipoItemEvolucao tipoItem : tipoItems) {
				RelatorioAnaEvoInternacaoVO vo = new RelatorioAnaEvoInternacaoVO();
				vo.setResponsavel(responsavel);
				vo.setVisto(visto);
				vo.setDthrValidaAna(relatorioAnaEvoInternacaoVO.getDthrValidaAna());
				vo.setRgtSeq(relatorioAnaEvoInternacaoVO.getRgtSeq());
				vo.setEvoSeq(relatorioAnaEvoInternacaoVO.getEvoSeq());
				vo.setCodPac(relatorioAnaEvoInternacaoVO.getCodPac());
				vo.setTieDescricao(tipoItem.getDescricao());
				vo.setTieOrdem(tipoItem.getOrdem());
				vo.setTieSeq(tipoItem.getSeq());
				retorno.add(vo);
			}
		}
		
		LOG.debug("retorno size: " + retorno.size());
		
		return retorno;
	}
	
	@Override
	public void complementarDadosRelatorioAnamneses(RelatorioAnaEvoInternacaoVO vo, AghParametros parametroMed, AghParametros parametroEnf, AghParametros parametroNut) throws ApplicationBusinessException {
		try {
			beginTransaction(TEMPO_12_HORAS);
			executaComplementarDadosRelatorioAnamneses(vo, parametroMed, parametroEnf, parametroNut);
			commitTransaction();
		} catch (ApplicationBusinessException e) {
			rollbackTransaction();
			throw e;
		}
	}

	private void executaComplementarDadosRelatorioAnamneses(RelatorioAnaEvoInternacaoVO vo, AghParametros parametroMed, AghParametros parametroEnf, AghParametros parametroNut) throws ApplicationBusinessException {
		//ITEM 2
		vo.setGrupoAnamnese(getRelatorioAnaEvoInternacaoRN().getGrupoProfissional(vo.getAnaSeq(), parametroMed, parametroEnf, parametroNut));
		//ITEM 3
		vo.setTinConteudo(getRelatorioAnaEvoInternacaoRN().obterEmgVisaoAnamnese(vo.getAnaSeq(),vo.getTinSeq(),vo.getRgtSeq(), Boolean.FALSE, "S"));
		//ITEM 4
		vo.setResponsavel(obterIdentidadeResponsavel(null, vo.getAnaSeq()));
		//ITEM 5
		vo.setVisto(getProntuarioOnlineFacade().visualizarNotaAnamneseEMG(vo.getRgtSeq(), null));
		
		vo.setTriagem(false);
	}
	
	private String obterIdentidadeResponsavel(Long evoSeq, Long anaSeq)
			throws ApplicationBusinessException {
		String identResp = "";
		
		if (evoSeq != null) {
			MamEvolucoes evo = getMamEvolucoesDAO().obterPorChavePrimaria(evoSeq);
			identResp = getAtendimentoPacientesAgendadosRN().obterIdentificacaoResponsavel(null, evo);
		} else if (anaSeq != null) {
			MamAnamneses  ana = getMamAnamnesesDAO().obterPorChavePrimaria(anaSeq);
			identResp = getAtendimentoPacientesAgendadosRN().obterIdentificacaoResponsavel(ana, null);
		}
		
		return identResp;
	}
	
	@Override
	public void complementarDadosRelatorioEvolucao(RelatorioAnaEvoInternacaoVO vo, CseCategoriaProfissional categoriaProfissional) throws ApplicationBusinessException {
		try {
			beginTransaction(TEMPO_12_HORAS);
			executaComplementarDadosRelatorioEvolucao(vo, categoriaProfissional);
			commitTransaction();
		} catch (ApplicationBusinessException e) {
			rollbackTransaction();
			throw e;
		}
	}

	private void executaComplementarDadosRelatorioEvolucao(RelatorioAnaEvoInternacaoVO vo, CseCategoriaProfissional categoriaProfissional) throws ApplicationBusinessException {
		//ITEM 3
		vo.setTieConteudo(getRelatorioAnaEvoInternacaoRN().obterEmgVisaoEvolucao(vo.getEvoSeq(), vo.getTieSeq(), vo.getRgtSeq(), Boolean.FALSE, "S", categoriaProfissional));
		//ITEM 4
	//	vo.setResponsavel(obterIdentidadeResponsavel(vo.getEvoSeq(), null, servidorLogado));
		//ITEM 5
	//	vo.setVisto(getProntuarioOnlineFacade().visualizarNotaEvolucaoEMG(vo.getRgtSeq(), null, servidorLogado));
		
		vo.setTriagem(false);
	}
	
	private List<RelatorioAnaEvoInternacaoVO> pesquisarRelatorioEvolucao(Integer atdSeq, CseCategoriaProfissional categoriaProfissional) throws ApplicationBusinessException {
		List<RelatorioAnaEvoInternacaoVO> result = new ArrayList<RelatorioAnaEvoInternacaoVO>();
		
		result = executaComplementarDadosComTipoItemEvolucao(getMamEvolucoesDAO().pesquisarEvolucoesPorAtendimentoRelatorioAnaEvoInternacao(atdSeq));
		
		int size = result.size();
		
		for (int i = 0; i < size; i++) {
			if ((i + 1) % 100 == 0) {
				LOG.debug("pesquisarRelatorioEvolucao " + (i + 1) + "/" + size);
			}
			executaComplementarDadosRelatorioEvolucao(result.get(i), categoriaProfissional);
		}
		
		if (!result.isEmpty()) {
			executaComplementarRodapeRelatorioAnaEvoInternacao(result.get(0), Boolean.FALSE);
		}		
		
		return result;
	}
	
	private List<RelatorioAnaEvoInternacaoVO> pesquisarRelatorioEvolucaoPeriodo(Integer atdSeq, Date dataInicial, Date dataFinal, CseCategoriaProfissional categoriaProfissional) throws ApplicationBusinessException {
		if (DateUtil.calcularDiasEntreDatas(dataInicial, dataFinal) > 30){
			throw new ApplicationBusinessException(RelatorioAnaEvoInternacaoBeanExceptionCode.RELATORIO_EVOLUCAO_PERIODO_MAIOR_30_DIAS);
		}
		
		List<RelatorioAnaEvoInternacaoVO> result = new ArrayList<RelatorioAnaEvoInternacaoVO>();
		result = executaComplementarDadosComTipoItemEvolucao(getMamEvolucoesDAO().pesquisarEvolucoesPorAtendimentoRelatorioAnaEvoInternacaoPeriodo(atdSeq, dataInicial, dataFinal));
		for (RelatorioAnaEvoInternacaoVO vo : result) {
			executaComplementarDadosRelatorioEvolucao(vo, categoriaProfissional);
		}
		
		if (!result.isEmpty()) {
			executaComplementarRodapeRelatorioAnaEvoInternacao(result.get(0), Boolean.FALSE);
		}

		return result;
	}
	
	@Override
	public void complementarRodapeRelatorioAnaEvoInternacao(RelatorioAnaEvoInternacaoVO vo, Boolean emergencia) throws ApplicationBusinessException {
		beginTransaction(TEMPO_12_HORAS);
		executaComplementarRodapeRelatorioAnaEvoInternacao(vo, emergencia);		
		commitTransaction();
	}

	private void executaComplementarRodapeRelatorioAnaEvoInternacao(
			RelatorioAnaEvoInternacaoVO vo, Boolean emergencia) {
		//ITEM 7 e 9
		incluirDadosPaciente(vo);
		//ITEM 8
		vo.setAgenda(obterAgenda(vo.getConNumero(), vo.getRgtSeq(), emergencia));
		//ITEM 10
		vo.setRodape(obterRodape(vo, emergencia));
	}
	
	private void incluirDadosPaciente(RelatorioAnaEvoInternacaoVO vo) {
		AipPacientes pac = getPacienteFacade().obterAipPacientesPorChavePrimaria(vo.getCodPac());
		vo.setPaciente(getRelatorioAnaEvoInternacaoRN().mpmcMinusculo(pac.getNome(),2));
		vo.setProntuario(CoreUtil.formataProntuario(pac.getProntuario()));
		vo.setDtNascimento(pac.getDtNascimento());
	}
	
	private String obterAgenda(Integer conNumero, Long rgtSeq, Boolean emergencia) {
		if (emergencia) {
			return obterEspecialidadeEmergencia(conNumero);
		} else {
			MamRegistro registro = getMamRegistroDAO().obterPorChavePrimaria(rgtSeq);
			if (registro != null && registro.getAtendimento() != null && registro.getAtendimento().getLeito() != null) {
				return registro.getAtendimento().getLeito().getLeitoID();
			}			
		}
		return "";
	}

	private String obterEspecialidadeEmergencia(Integer conNumero) {
		AacConsultas con = getAacConsultasDAO().obterPorChavePrimaria(conNumero);
		if (con != null && con.getGradeAgendamenConsulta() != null && con.getGradeAgendamenConsulta().getEspecialidade() != null) {
				return con.getGradeAgendamenConsulta().getEspecialidade().getSigla();
		}				
		return "";
	}
	
	private String obterRodape(RelatorioAnaEvoInternacaoVO vo, Boolean emergencia) {
		StringBuilder sb = new StringBuilder(17);
		
			sb.append("Código: ")
			.append(vo.getCodPac());
			if (emergencia) {
				sb.append(" BA: ")
				.append(getRelatorioAnaEvoInternacaoRN().obterConNumeroAtendimentoEmergencia(vo.getTrgSeq()));
			}
			sb.append("  Idade: ")
			.append(getRelatorioAnaEvoInternacaoRN().obterIdadeMesDias(vo.getDtNascimento(), new Date()));
		
		return sb.toString();
	}
	
	protected ICascaFacade getCascaFacade() {
		return this.cascaFacade;
	}
	
	protected AacConsultasDAO getAacConsultasDAO() {
		return aacConsultasDAO;
	}
	
	protected MamAnamnesesDAO getMamAnamnesesDAO() {
		return mamAnamnesesDAO;
	}
	
	protected MamEvolucoesDAO getMamEvolucoesDAO() {
		return mamEvolucoesDAO;
	}
	
	protected MamRegistroDAO getMamRegistroDAO() {
		return mamRegistroDAO;
	}
	
	protected MamTipoItemAnamnesesDAO getMamTipoItemAnamnesesDAO() {
		return mamTipoItemAnamnesesDAO;
	}
	
	protected MamTipoItemEvolucaoDAO getMamTipoItemEvolucaoDAO() {
		return mamTipoItemEvolucaoDAO;
	}

	protected AtendimentoPacientesAgendadosRN getAtendimentoPacientesAgendadosRN() {
		return atendimentoPacientesAgendadosRN;
	}
	
	protected IProntuarioOnlineFacade getProntuarioOnlineFacade() {
		return prontuarioOnlineFacade;
	}
	
	protected RelatorioAnaEvoInternacaoRN getRelatorioAnaEvoInternacaoRN() {
		return relatorioAnaEvoInternacaoRN;
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
			
}
