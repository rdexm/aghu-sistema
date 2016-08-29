package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcControleEscalaCirurgicaDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioTipoEscala;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipAvisoAgendamentoCirurgia;
import br.gov.mec.aghu.model.AipMovimentacaoProntuarios;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Classe responsável pelas regras de FORMS de #26775: 
 * Aviso SAMIS ao salvar o agendamento do procedimentos eletivo, urgência ou emergência
 * 
 * @author ihaas
 * 
 */
@Stateless
public class AvisoAgendamentoCirurgiaON extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(AvisoAgendamentoCirurgiaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcControleEscalaCirurgicaDAO mbcControleEscalaCirurgicaDAO;


	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	@EJB
	private IPacienteFacade iPacienteFacade;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private IAghuFacade iAghuFacade;

	@EJB
	private IBlocoCirurgicoFacade iBlocoCirurgicoFacade;

	private static final long serialVersionUID = -171704594916619663L;

	public void gerarAvisoSamis(Integer crgSeq) throws ApplicationBusinessException, ApplicationBusinessException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();

		if (getBlocoCirurgicoFacade().temTarget("gerarAvisoSamisAoSalvarAgendamento", "gerarAviso")) {
			AghParametros paramAvisoCirurgiaSamis = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_AVISO_CIRURGIA_SAMIS);
			if (paramAvisoCirurgiaSamis != null && paramAvisoCirurgiaSamis.getVlrNumerico().intValue() == 1) {
				MbcCirurgias cirurgia = this.getMbcCirurgiasDAO().obterPorChavePrimaria(crgSeq);
				// Se paciente não tem prontuário, não emitir aviso para o SAMIS.
				if (cirurgia.getPaciente().getProntuario() == null) {
					return;
				}
				// Se não executou a escala do dia, não emitir aviso para o SAMIS.
				Short unfSeq = cirurgia.getUnidadeFuncional().getSeq();
				Date dataCirurgiaTruncada = DateUtil.truncaData(cirurgia.getData());
				if (!this.getMbcControleEscalaCirurgicaDAO()
						.verificaExistenciaPeviaDefinitivaPorUNFData(unfSeq, dataCirurgiaTruncada, DominioTipoEscala.D)) {
					return;
				}
				// Se o paciente está internado ou em SO, não emitir aviso para o SAMIS.
				List<AghAtendimentos> atendimentos = this.getAghuFacade()
				.pesquisaAtendimentoPacienteInternadoUrgencia(cirurgia.getPaciente().getCodigo());
				if (atendimentos != null && !atendimentos.isEmpty()) {
					return;
				}
				// Se o paciente está no CO ou emergência, não emitir aviso para o SAMIS.
				AghParametros paramCodEvento = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_EVENTO_CO);
				AghParametros paramOrigemEmergencia = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_ORIGEM_EMERGENCIA);

				List<AipMovimentacaoProntuarios> movimentacoes = this.getPacienteFacade()
				.listarMovimentacoesProntuariosPorCodigoPacienteESituacao(cirurgia.getPaciente().getCodigo(), 
						paramCodEvento.getVlrNumerico().intValue(), paramOrigemEmergencia.getVlrNumerico().intValue());

				if (movimentacoes != null && !movimentacoes.isEmpty()) {
					return;
				}
				// Se o agendamento da cirurgia for retroativo, não emitir aviso para o SAMIS.
				if (DateUtil.validaDataMenor(DateUtil.truncaData(cirurgia.getData()), DateUtil.truncaData(new Date()))) {
					return;
				}
				// Se o paciente agendado já tem cirurgia na mesma data, não emitir aviso para o SAMIS.
				List<MbcCirurgias> cirurgiasExistentes = this.getMbcCirurgiasDAO()
				.pesquisarCirurgiasPorPacienteDataCrg(cirurgia.getSeq(), cirurgia.getPaciente().getCodigo(), cirurgia.getData());
				if (cirurgiasExistentes != null && !cirurgiasExistentes.isEmpty()) {
					return;
				}
				// Realiza o insert na tabela AIP_AVISO_AGENDAMENTO_CIRURGIA
				AipAvisoAgendamentoCirurgia avisoAgendamentoCirurgia = new AipAvisoAgendamentoCirurgia();
				avisoAgendamentoCirurgia.setMbcCirurgias(cirurgia);
				avisoAgendamentoCirurgia.setRapServidores(getRegistroColaboradorFacade().obterServidorPorUsuario(servidorLogado.getUsuario()));
				avisoAgendamentoCirurgia.setCriadoEm(new Date());
				avisoAgendamentoCirurgia.setStatus("0");
				avisoAgendamentoCirurgia.setVersion(0);
				this.getPacienteFacade().persistirAvisoAgendamentoCirurgia(avisoAgendamentoCirurgia);
			}
		}
	}

	/*
	 * Getters Facades, RNs e DAOs
	 */

	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}

	protected IPacienteFacade getPacienteFacade() {
		return iPacienteFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}

	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return iBlocoCirurgicoFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}

	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}

	protected MbcControleEscalaCirurgicaDAO getMbcControleEscalaCirurgicaDAO() {
		return mbcControleEscalaCirurgicaDAO;
	}

}
