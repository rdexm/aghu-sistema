package br.gov.mec.aghu.blococirurgico.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcExtratoCirurgiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.vo.MonitorCirurgiaConcluidaHojeVO;
import br.gov.mec.aghu.blococirurgico.vo.MonitorCirurgiaSalaCirurgiaVO;
import br.gov.mec.aghu.blococirurgico.vo.MonitorCirurgiaSalaPreparoVO;
import br.gov.mec.aghu.blococirurgico.vo.MonitorCirurgiaSalaRecuperacaoVO;
import br.gov.mec.aghu.blococirurgico.vo.MonitorCirurgiaVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Classe responsável pelas regras de FORMS MBCF_MONITOR de #27171 – Painel para exibição do status do paciente durante o período em que esteve no centro cirúrgico - Monitor
 * 
 * @author aghu
 * 
 */
@Stateless
public class MonitorCirurgiaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MonitorCirurgiaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcProfCirurgiasDAO mbcProfCirurgiasDAO;

	@Inject
	private MbcExtratoCirurgiaDAO mbcExtratoCirurgiaDAO;


	@EJB
	private EscalaCirurgiasON escalaCirurgiasON;

	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	@EJB
	private NomeComPontoRN nomeComPontoRN;

	@EJB
	private IParametroFacade iParametroFacade;

	private static final long serialVersionUID = 9013224598946276096L;
	private static final int QTDE_PADRAO_PAGINACAO = 10;

	/**
	 * Pesquisa 1 - Pacientes que estão em sala de preparado aguardando o início da cirurgia
	 * 
	 * @param unfSeq
	 * @return
	 */
		public List<MonitorCirurgiaSalaPreparoVO> pesquisarMonitorCirurgiaSalaPreparo(final Short unfSeq) {
			List<MonitorCirurgiaSalaPreparoVO> resultado = this.getMbcExtratoCirurgiaDAO().pesquisarMonitorCirurgiaSalaPreparo(unfSeq);
			for (MonitorCirurgiaSalaPreparoVO vo : resultado) {
				// Chamada para FUNCTION MBCC_NOME_PONTO
				vo.setNomePaciente(this.getNomeComPontoRN().obterNomeComPonto(vo.getCrgSeq()));
			}
			Collections.sort(resultado); // Ordena por nome do paciente
			return resultado;
		}

	/**
	 * Pesquisa 2 - Pacientes que estão em sala de cirurgia
	 * 
	 * @param unfSeq
	 * @return
	 */
	public List<MonitorCirurgiaSalaCirurgiaVO> pesquisarMonitorPacientesSalaCirurgia(final Short unfSeq) {
		List<MbcProfCirurgias> listaExtratoCirurgias = this.getMbcProfCirurgiasDAO().pesquisarMonitorPacientesSalaCirurgia(unfSeq);
		List<MonitorCirurgiaSalaCirurgiaVO> resultado = new ArrayList<MonitorCirurgiaSalaCirurgiaVO>();
		for (MbcProfCirurgias profCirurgias : listaExtratoCirurgias) {
			MonitorCirurgiaSalaCirurgiaVO vo = new MonitorCirurgiaSalaCirurgiaVO();
			vo.setCrgSeq(profCirurgias.getId().getCrgSeq());
			// Chamada para FUNCTION MBCC_NOME_PONTO
			vo.setNomePaciente(this.getNomeComPontoRN().obterNomeComPonto(vo.getCrgSeq()));
			vo.setEntradaSala(profCirurgias.getCirurgia().getDataEntradaSala());
			String equipe = this.getRegistroColaboradorFacade().obterNomeEquipeProfissionalMonitorCirurgia(profCirurgias);
			if (equipe != null) {
				vo.setEquipe(equipe);
			}
			resultado.add(vo);
		}
		Collections.sort(resultado); // Ordena por nome do paciente
		return resultado;
	}

	/**
	 * Pesquisa 3 - Pacientes que estão em sala de recuperação
	 * 
	 * @param unfSeq
	 * @return
	 */
	public List<MonitorCirurgiaSalaRecuperacaoVO> pesquisarMonitorCirurgiaSalaRecuperacao(final Short unfSeq) {
		List<MonitorCirurgiaSalaRecuperacaoVO> resultado = this.getMbcCirurgiasDAO().pesquisarMonitorCirurgiaSalaRecuperacao(unfSeq);
		for (MonitorCirurgiaSalaRecuperacaoVO vo : resultado) {
			// Chamada para FUNCTION MBCC_NOME_PONTO
			vo.setNomePaciente(this.getNomeComPontoRN().obterNomeComPonto(vo.getCrgSeq()));
		}
		Collections.sort(resultado); // Ordena por nome do paciente
		return resultado;
	}

	/**
	 * Pesquisa 4 - Pacientes com cirurgias concluídas do dia
	 * 
	 * @param unfSeq
	 * @return
	 * @throws BaseException
	 */
	public List<MonitorCirurgiaConcluidaHojeVO> pesquisarMonitorCirurgiaConcluidaHoje(final Short unfSeq) throws BaseException {

		AghParametros pDestSalaRecuperacao = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DEST_SALA_REC);
		AghParametros pDestObito = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DEST_OBITO);
		AghParametros pDestUnidade = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DEST_UNID);
		Byte[] destinosPaciente = new Byte[] { pDestSalaRecuperacao.getVlrNumerico().byteValue(), pDestObito.getVlrNumerico().byteValue(),
				pDestUnidade.getVlrNumerico().byteValue() };

		List<MonitorCirurgiaConcluidaHojeVO> resultado = this.getMbcCirurgiasDAO().pesquisarMonitorCirurgiaConcluidaHoje(unfSeq, destinosPaciente);
		for (MonitorCirurgiaConcluidaHojeVO vo : resultado) {
			// Chamada para FUNCTION MBCC_NOME_PONTO
			vo.setNomePaciente(this.getNomeComPontoRN().obterNomeComPonto(vo.getCrgSeq()));
			// Chamada para FUNCTION MBCC_LOCAL_AIP_PAC
			vo.setLocal(this.getEscalaCirurgiasON().pesquisaQuarto(vo.getCodigoPaciente()));
		}
		Collections.sort(resultado); // Ordena por nome do paciente
		return resultado;
	}

	/**
	 * Obtem a lista paginada das pesquisas
	 * 
	 * @param <T>
	 * @param resultadoConsulta
	 * @return
	 */
	public <T extends MonitorCirurgiaVO> List<List<T>> obterListaResultadoPaginado(List<T> resultadoConsulta) throws BaseException {
		List<List<T>> resultadoPaginado = new ArrayList<List<T>>();
		List<T> novaLista = new ArrayList<T>();

		int qtdePadraoPaginacao = QTDE_PADRAO_PAGINACAO;
		AghParametros parametroQtdePadrao = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_QTDE_PADRAO_PAGINACAO_MONITOR_CIRURGIA);
		if (parametroQtdePadrao != null && CoreUtil.maior(parametroQtdePadrao.getVlrNumerico().intValue(), 0)) {
			qtdePadraoPaginacao = parametroQtdePadrao.getVlrNumerico().intValue();
		}

		for (int i = 0; i < resultadoConsulta.size(); i++) {
			novaLista.add(resultadoConsulta.get(i));
			if (novaLista.size() >= qtdePadraoPaginacao) {
				novaLista = new ArrayList<T>();
			}
			if (!resultadoPaginado.contains(novaLista) && !novaLista.isEmpty()) {
				resultadoPaginado.add(novaLista);
			}
		}
		return resultadoPaginado;
	}

	/*
	 * Getters Facades, RNs e DAOs
	 */

	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}

	protected MbcExtratoCirurgiaDAO getMbcExtratoCirurgiaDAO() {
		return mbcExtratoCirurgiaDAO;
	}

	protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
		return mbcProfCirurgiasDAO;
	}

	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}

	protected NomeComPontoRN getNomeComPontoRN() {
		return nomeComPontoRN;
	}

	protected EscalaCirurgiasON getEscalaCirurgiasON() {
		return escalaCirurgiasON;
	}
}