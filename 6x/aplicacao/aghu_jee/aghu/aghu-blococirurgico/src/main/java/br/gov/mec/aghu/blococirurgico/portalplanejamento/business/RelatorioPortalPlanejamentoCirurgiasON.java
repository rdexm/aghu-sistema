package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaAnotacaoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.RelatorioPortalPlanejamentoCirurgiasVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcAgendaAnotacao;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgsId;
import br.gov.mec.aghu.util.CapitalizeEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Classe responsável por prover os métodos de negócio genéricos para o
 * relatório do Portal de Planejamento de Cirurgias.
 * 
 * @author heliz.neves
 * 
 */
@Stateless
public class RelatorioPortalPlanejamentoCirurgiasON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RelatorioPortalPlanejamentoCirurgiasON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAgendasDAO mbcAgendasDAO;

	@Inject
	private MbcAgendaAnotacaoDAO mbcAgendaAnotacaoDAO;


	@EJB
	private IAmbulatorioFacade iAmbulatorioFacade;

	@EJB
	private RelatorioPortalPlanejamentoCirurgiasRN relatorioPortalPlanejamentoCirurgiasRN;

	@EJB
	private IAghuFacade iAghuFacade;

	@EJB
	private IBlocoCirurgicoFacade iBlocoCirurgicoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5422856624813137110L;

	public Boolean listarEquipePlanejamentoCirurgiasPossuiRegistro(Date dtIniAgenda,
			Date dtFimAgenda, Integer pucSerMatricula, Short pucSerVinCodigo,
			Short pucUnfSeq, DominioFuncaoProfissional pucIndFuncaoProf,
			Short espSeq, Short unfSeq, String pEquipe)
			throws ApplicationBusinessException {
		Long count = this.getMbcAgendasDAO()
				.pesquisarPlanejamentoCirurgiaAgendadaCount(dtIniAgenda,
						dtFimAgenda, pucSerMatricula, pucSerVinCodigo,
						pucUnfSeq, pucIndFuncaoProf, espSeq, unfSeq);
		if (count > 0) {
			return Boolean.TRUE;
		} else {
			MbcProfAtuaUnidCirgsId profAtuaUnidCirgsId = new MbcProfAtuaUnidCirgsId();
			profAtuaUnidCirgsId.setSerMatricula(pucSerMatricula);
			profAtuaUnidCirgsId.setSerVinCodigo(pucSerVinCodigo);
			profAtuaUnidCirgsId.setIndFuncaoProf(pucIndFuncaoProf);
			profAtuaUnidCirgsId.setUnfSeq(unfSeq);
			count = getMbcAgendaAnotacaoDAO()
					.pesquisarAgendaAnotacaoByPeriodoAndMbcProfAtuaCount(
							dtIniAgenda, dtFimAgenda, profAtuaUnidCirgsId);
			if (count > 0) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
	/**
	 * Método que retorna lista PlanejamentoCirurgiasVO para a geração do
	 * relatório.
	 * 
	 * @throws ApplicationBusinessException
	 * 
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	public List<RelatorioPortalPlanejamentoCirurgiasVO> listarEquipePlanejamentoCirurgias(
			Date dtIniAgenda, Date dtFimAgenda, Integer pucSerMatricula,
			Short pucSerVinCodigo, Short pucUnfSeq, DominioFuncaoProfissional pucIndFuncaoProf,
			Short espSeq, Short unfSeq, String pEquipe) throws ApplicationBusinessException
			{

		Set<RelatorioPortalPlanejamentoCirurgiasVO> listaCirurgiasVO = new HashSet<RelatorioPortalPlanejamentoCirurgiasVO>();

		MbcProfAtuaUnidCirgsId profAtuaUnidCirgsId = new MbcProfAtuaUnidCirgsId();
		profAtuaUnidCirgsId.setSerMatricula(pucSerMatricula);
		profAtuaUnidCirgsId.setSerVinCodigo(pucSerVinCodigo);
		profAtuaUnidCirgsId.setIndFuncaoProf(pucIndFuncaoProf);
		profAtuaUnidCirgsId.setUnfSeq(unfSeq);

		List<MbcAgendas> listaMbcAgendas = this.getMbcAgendasDAO()
				.pesquisarPlanejamentoCirurgiaAgendada(dtIniAgenda,
						dtFimAgenda, pucSerMatricula, pucSerVinCodigo,
						pucUnfSeq, pucIndFuncaoProf, espSeq, unfSeq);

		List<MbcAgendaAnotacao> listaMbcAgendaAnotacoes = getMbcAgendaAnotacaoDAO()
				.pesquisarAgendaAnotacaoByPeriodoAndMbcProfAtua(dtIniAgenda,
						dtFimAgenda, profAtuaUnidCirgsId);

		// 1
		AghUnidadesFuncionais aghUnidadeFuncional = getAghuFacade()
				.obterUnidadeFuncional(unfSeq);
		String unidadeFuncional = aghUnidadeFuncional.getDescricao();

		// 2
		AghEspecialidades aghEspecialidade = getAghuFacade()
				.obterAghEspecialidadesPorChavePrimaria(espSeq);
		String especialidade = aghEspecialidade.getNomeEspecialidade();

		for (MbcAgendas agenda : listaMbcAgendas) {
			RelatorioPortalPlanejamentoCirurgiasVO agendaVO = new RelatorioPortalPlanejamentoCirurgiasVO();

			processaMbcAgendas(agenda, agendaVO, especialidade,
					unidadeFuncional, pEquipe);

			listaCirurgiasVO.add(agendaVO);
		}

		for (MbcAgendaAnotacao agendaAnotacao : listaMbcAgendaAnotacoes) {
			RelatorioPortalPlanejamentoCirurgiasVO agendaAnotacaoVO = new RelatorioPortalPlanejamentoCirurgiasVO();

			processaMbcAgendaAnotacoes(agendaAnotacao, agendaAnotacaoVO,
					especialidade, unidadeFuncional, pEquipe);

			listaCirurgiasVO.add(agendaAnotacaoVO);
		}

		List<RelatorioPortalPlanejamentoCirurgiasVO> listaVoOrd = new ArrayList<RelatorioPortalPlanejamentoCirurgiasVO>(
				listaCirurgiasVO);
		CoreUtil.ordenarLista(listaVoOrd,RelatorioPortalPlanejamentoCirurgiasVO.Fields.DTHR_INCLUSAO.toString(),Boolean.TRUE);
		CoreUtil.ordenarLista(listaVoOrd,RelatorioPortalPlanejamentoCirurgiasVO.Fields.DTHR_PREV_INICIO.toString(),Boolean.TRUE);
		CoreUtil.ordenarLista(listaVoOrd,RelatorioPortalPlanejamentoCirurgiasVO.Fields.PRIORIDADE.toString(),Boolean.TRUE);
		CoreUtil.ordenarLista(listaVoOrd,RelatorioPortalPlanejamentoCirurgiasVO.Fields.IND_SITUACAO.toString(),Boolean.TRUE);
		CoreUtil.ordenarLista(listaVoOrd,RelatorioPortalPlanejamentoCirurgiasVO.Fields.ORDEM.toString(),Boolean.TRUE);
		CoreUtil.ordenarLista(listaVoOrd,RelatorioPortalPlanejamentoCirurgiasVO.Fields.DATA_AGENDA_ORDENAR.toString(),Boolean.TRUE);

		return listaVoOrd;
	}

	/**
	 * Popula VO com os dados de MbcAgendas para o relatório do Portal de
	 * Planejamento de Cirurgias.
	 * 
	 * @param RelatorioPortalPlanejamentoCirurgiasVO
	 */
	private void processaMbcAgendas(MbcAgendas agenda,
			RelatorioPortalPlanejamentoCirurgiasVO agendaVO, String especialidade,
			String unidadeFuncional, String pEquipe)
			throws ApplicationBusinessException {

		// 1
		agendaVO.setLocal(unidadeFuncional);

		// 2
		agendaVO.setEspecialidade(especialidade);

		// 3
		agendaVO.setEquipe(pEquipe);

		agendaVO.setSeq(agenda.getSeq());

		// 4
		agendaVO.setDataAgenda(DateFormatUtil.fomataDiaMesAno(agenda
				.getDtAgenda()));

		// Campo para ordenação da lista
		agendaVO.setDataAgendaOrdenar(DateUtil.dataToString(
				agenda.getDtAgenda(), "yyyyMMdd"));

		// 5 aelc_trata_nome(mpmc_minusculo(agenda.getPaciente().getNome(),2))
		// nome_paciente
		String nomePacienteCapitaliza = getAmbulatorioFacade()
		.obterDescricaoCidCapitalizada(agenda.getPaciente().getNome(), CapitalizeEnum.TODAS);
		
		agendaVO.setNomePaciente(getPortalPlanejamentoCirurgiasRN()
		.obterNomeIntermediarioPacienteAbreviado(nomePacienteCapitaliza));
		
		// 6
		agendaVO.setProntuario(CoreUtil.formataProntuario(agenda.getPaciente()
				.getProntuario()));

		// 7 mpmc_minusculo(pci.descricao,1)
		agendaVO.setProcedimento(getAmbulatorioFacade()
				.obterDescricaoCidCapitalizada(
						agenda.getProcedimentoCirurgico().getDescricao(),
						CapitalizeEnum.PRIMEIRA));

		// Campo para ordenação da lista
		agendaVO.setOrdem(BigDecimal.ONE.intValue());

		// 8
		String vTempoMinCirgRealizada = getPortalPlanejamentoCirurgiasRN()
				.obterTempoSalaCirurgia(agenda.getSeq(),
						agenda.getIntervaloEscala());
		String vTempoSala = DateUtil.dataToString(agenda.getTempoSala(),
				"HH:mm");
		agendaVO.setTempoSala((vTempoMinCirgRealizada != null) ? vTempoMinCirgRealizada
				: vTempoSala);

		// 9
		if (agenda.getSalaCirurgica() != null) {
			agendaVO.setSala(agenda.getSalaCirurgica().getId().getSeqp() == null ? null
				: " - Sala " + agenda.getSalaCirurgica().getId().getSeqp());
		}else{
			agendaVO.setSala("");
		}

		// 10
		agendaVO.setComentario(agenda.getComentario());

		// 11
		agendaVO.setAnotacoes(null);

		agendaVO.setIndSituacao(agenda.getIndSituacao());

		agendaVO.setPrioridade(agenda.getIndPrioridade() == Boolean.TRUE ? 1
				: 2);

		agendaVO.setDthrPrevInicio(agenda.getDthrPrevInicio());

		agendaVO.setDthrInclusao(agenda.getDthrInclusao());
	}

	/**
	 * Popula VO com os dados de MbcAgendas para o relatório do Portal de
	 * Planejamento de Cirurgias.
	 * 
	 * @param RelatorioPortalPlanejamentoCirurgiasVO
	 */
	private void processaMbcAgendaAnotacoes(MbcAgendaAnotacao agendaAnotacao,
			RelatorioPortalPlanejamentoCirurgiasVO agendaAnotacaoVO, String especialidade,
			String unidadeFuncional, String pEquipe)
			throws ApplicationBusinessException {
		// 1
		agendaAnotacaoVO.setLocal(unidadeFuncional);

		// 2
		agendaAnotacaoVO.setEspecialidade(especialidade);

		// 3
		agendaAnotacaoVO.setEquipe(pEquipe);

		agendaAnotacaoVO.setSeq(Integer.valueOf(0));

		// 4
		agendaAnotacaoVO.setDataAgenda(DateFormatUtil
				.fomataDiaMesAno(agendaAnotacao.getId().getData()));

		// Campo para ordenação da lista
		agendaAnotacaoVO.setDataAgendaOrdenar(DateUtil.dataToString(
				agendaAnotacao.getId().getData(), "yyyyMMdd"));

		// 5
		agendaAnotacaoVO.setNomePaciente(null);

		// 6
		agendaAnotacaoVO.setProntuario(null);

		// 7
		agendaAnotacaoVO.setProcedimento(null);

		// Campo para ordenação da lista
		agendaAnotacaoVO.setOrdem(Integer.valueOf(2));

		// 8
		agendaAnotacaoVO.setTempoSala(null);

		// 9
		agendaAnotacaoVO.setSala("");

		// 10
		agendaAnotacaoVO.setComentario(null);

		// 11
		agendaAnotacaoVO.setAnotacoes(agendaAnotacao.getDescricao());

		agendaAnotacaoVO.setIndSituacao(null);

		agendaAnotacaoVO.setPrioridade(2);

		agendaAnotacaoVO.setDthrPrevInicio(null);

		agendaAnotacaoVO.setDthrInclusao(new Date()); 
	}

	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}

	protected MbcAgendasDAO getMbcAgendasDAO() {
		return mbcAgendasDAO;
	}

	protected MbcAgendaAnotacaoDAO getMbcAgendaAnotacaoDAO() {
		return mbcAgendaAnotacaoDAO;
	}

	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.iAmbulatorioFacade;
	}

	public IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return this.iBlocoCirurgicoFacade;
	}

	protected RelatorioPortalPlanejamentoCirurgiasRN getPortalPlanejamentoCirurgiasRN() {
		return relatorioPortalPlanejamentoCirurgiasRN;
	}
}