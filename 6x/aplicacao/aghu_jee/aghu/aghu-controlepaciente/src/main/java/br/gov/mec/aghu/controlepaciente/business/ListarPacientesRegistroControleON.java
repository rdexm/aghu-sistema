package br.gov.mec.aghu.controlepaciente.business;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.controlepaciente.dao.EcpHorarioControleDAO;
import br.gov.mec.aghu.controlepaciente.vo.PacienteInternadoVO;
import br.gov.mec.aghu.controlepaciente.vo.PacienteInternadoVO.StatusSinalizadorUP;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class ListarPacientesRegistroControleON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ListarPacientesRegistroControleON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private EcpHorarioControleDAO ecpHorarioControleDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;
	
	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3799435925359140367L;

	/**
	 * Realizar a pesquisa dos atendimentos que deverão ser apresentados na
	 * lista.
	 * 
	 * @param servidor
	 * @return
	 * @throws BaseException
	 */
	public List<PacienteInternadoVO> pesquisarPacientesInternados(
			RapServidores servidor) throws BaseException {
		
		if (servidor == null) {
			throw new IllegalArgumentException("Argumento inválido.");
		}

		List<PacienteInternadoVO> listaPacientesInternadosVO = this
				.getAghuFacade().listarControlePacientesInternados(servidor);

		String paramIntegraChecagem = this.obterParametroIntegraChecagem();
		Short paramControleUP = this.obterParametroControleUP();
		BigDecimal paramLinhaCorteUP = this.obterParametroLinhaCorteUP();

		for (PacienteInternadoVO lista : listaPacientesInternadosVO) {
			boolean hasSumarioAlta = this
					.getPrescricaoMedicaFacade()
					.existeAltaSumarioConcluidaPorAtendimento(lista.getAtdSeq());
			lista.setSumarioAlta(hasSumarioAlta);
			lista.setDisableIconeChecagem(paramIntegraChecagem.equals("S") ? this
					.disableIconeChecagem(lista.getUnfSeq()) : true);
			lista.setSinalizadorUlceraPressao(this.buscarSinalizadorUP(
					lista.getAtdSeq(), paramControleUP, paramLinhaCorteUP));
			lista.setPacienteNotifGMR(this.getPesquisaInternacaoFacade().pacienteNotifGMR(lista.getPacCodigo()));
		}

		return listaPacientesInternadosVO;

	}

	/**
	 * Identificar se o ícone de checagem será apresentado na lista
	 * 
	 * @param atendimento
	 * @return
	 * @throws BaseException
	 */
	public boolean disableIconeChecagem(Short unfSeq) throws BaseException {

		if (unfSeq == null) {
			throw new IllegalArgumentException("Argumento inválido.");
		}

		// verifica se a unidade tem caracteristicas para checagem eletrônica
		boolean hasCaractChecagem = this
				.getAghuFacade()
				.unidadeFuncionalPossuiCaracteristica(
						unfSeq,
						ConstanteAghCaractUnidFuncionais.CHECAGEM_ELETRONICA,
						ConstanteAghCaractUnidFuncionais.CHECAGEM_ELETRONICA_ENFERMAGEM);
		if (hasCaractChecagem) {
			return false;
		}

		return true;
	}

	/**
	 * Realiza a busca do parâmetro que informa se a checagem eletrônica está
	 * integrada ao AGHU. Caso o parâmetro não esteja definido ou esteja NULL,
	 * retorna 'N';
	 * 
	 * @return
	 * @throws BaseException
	 */
	protected String obterParametroIntegraChecagem() throws BaseException {

		AghParametros aghParametro = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_AGHU_INTEGRA_CHECAGEM);

		return (aghParametro != null && aghParametro.getVlrTexto() != null) ? aghParametro
				.getVlrTexto() : DominioSimNao.N.toString();
	}

	/**
	 * Realiza a busca do parâmetro que informa o seq do Item de Controle de
	 * Úlcera de Pressão;
	 * 
	 * @return
	 * @throws BaseException
	 */
	protected Short obterParametroControleUP() throws BaseException {

		AghParametros aghParametro = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_AGHU_CONTROLES_UP);

		return (aghParametro != null && aghParametro.getVlrNumerico() != null) ? aghParametro
				.getVlrNumerico().shortValue() : null;

	}

	/**
	 * Realiza a busca do parâmetro que informa o valor de corte para
	 * determiniar Úlcera de Pressão;
	 * 
	 * @return
	 * @throws BaseException
	 */
	protected BigDecimal obterParametroLinhaCorteUP() throws BaseException {

		AghParametros aghParametro = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_AGHU_LINHA_CORTE_UP);

		return aghParametro != null && aghParametro.getVlrNumerico() != null ? aghParametro
				.getVlrNumerico() : null;
	}

	/**
	 * Buscar o sinalizador de úlcera de pressão que servirá para definir o
	 * ícone e o tooltip que serão apresentados na lista;
	 * 
	 * @param atendimento
	 * @param paramControleUP
	 * @param paramLinhaCorteUP
	 * @return
	 */
	protected StatusSinalizadorUP buscarSinalizadorUP(Integer atdSeq,
			Short paramControleUP, BigDecimal paramLinhaCorteUP) {

		if (this.getPrescricaoEnfermagemFacade()
				.existeComunicadoUlceraPressaoPorAtendimento(atdSeq)) {
			return StatusSinalizadorUP.FLAG_VERMELHO;
		}

		// Somente executa a regra das sinalizações amarelas e verdes quando
		// os parâmetros estiverem setados
		if (paramControleUP != null && paramLinhaCorteUP != null) {
			BigDecimal medicao = this.getEcpHorarioControleDAO()
					.buscarMedicaoUlceraPressaoPorAtendimento(atdSeq,
							paramControleUP);

			if (medicao == null) {
				return null;
			}

			if (medicao.compareTo(paramLinhaCorteUP) <= 0) {
				return StatusSinalizadorUP.FLAG_AMARELO;
			} else {
				return StatusSinalizadorUP.FLAG_VERDE;
			}

		}

		return null;

	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	protected EcpHorarioControleDAO getEcpHorarioControleDAO() {
		return ecpHorarioControleDAO;
	}

	protected IPrescricaoEnfermagemFacade getPrescricaoEnfermagemFacade(){
		return prescricaoEnfermagemFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	public IPesquisaInternacaoFacade getPesquisaInternacaoFacade() {
		return pesquisaInternacaoFacade;
	}
}
