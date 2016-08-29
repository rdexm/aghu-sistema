package br.gov.mec.aghu.prescricaomedica.modelobasico.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MpaCadOrdCuidado;
import br.gov.mec.aghu.model.MpaUsoOrdCuidado;
import br.gov.mec.aghu.model.MpmCuidadoUsual;
import br.gov.mec.aghu.model.MpmCuidadoUsualUnf;
import br.gov.mec.aghu.model.MpmMensPrcrCuidado;
import br.gov.mec.aghu.model.MpmModeloBasicoCuidado;
import br.gov.mec.aghu.model.MpmPrescricaoCuidado;
import br.gov.mec.aghu.model.MptPrescricaoCuidado;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmCuidadoUsualDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmCuidadoUsualUnfDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmServidorUnidFuncionalDAO;

/**
 * @author mgoulart
 * 
 */
@Stateless
public class PesquisarCuidadoUsualON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(PesquisarCuidadoUsualON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@Inject
	private MpmCuidadoUsualDAO mpmCuidadoUsualDAO;
	
	@Inject
	private MpmCuidadoUsualUnfDAO mpmCuidadoUsualUnfDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private MpmServidorUnidFuncionalDAO mpmServidorUnidFuncionalDAO;
	/**
	 * 
	 */
	private static final long serialVersionUID = -2384634532240508847L;

	public enum PesquisarCuidadoUsualONExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_SERVIDOR_INVALIDO, MENSAGEM_CUIDADO_USUAL_PARAMETRO_OBRIGATORIO, //
		 MENSAGEM_CUIDADO_USUAL_POSSUI_UNIDADES_FUNCIONAIS, //
		MENSAGEM_CUIDADO_USUAL_JA_PRESCRITO, MENSAGEM_CUIDADO_USUAL_JA_UTILIZADO_MODELO_BASICO_PRESCRICAO, //
		MENSAGEM_CUIDADO_USUAL_JA_UTILIZADO_PRESCRICAO_CUIDADOS, //
		MENSAGEM_CUIDADO_USUAL_JA_UTILIZADO_CADASTRO_ORDEM_CUIDADOS, MENSAGEM_CUIDADO_USUAL_JA_UTILIZADO_ORDEM_CUIDADOS, //
		ERRO_RECUPERACAO_PARAM_P_DIAS_PERM_MPM, // MPM-00680
		REGISTRO_FORA_PERIODO_PERMITIDO_PARA_EXCLUSAO, // MPM-00681
		ERRO_EXCLUSAO_PROCEDIMENTOS_HOSPITALARES_INTERNOS
		// FAT-00154
	}

	/**
	 * Exclui cuidado usual da prescrição.
	 * 
	 * @param seqCuidado
	 * @throws ApplicationBusinessException
	 *             sem existir itens associados
	 */
	public void excluir(Integer seqCuidado) throws ApplicationBusinessException {

		if (seqCuidado == null) {
			throw new ApplicationBusinessException(
					PesquisarCuidadoUsualONExceptionCode.MENSAGEM_CUIDADO_USUAL_PARAMETRO_OBRIGATORIO);
		}

		MpmCuidadoUsual mpmCuidadoUsual = this.getMpmCuidadoUsualDAO()
				.obterPorChavePrimaria(seqCuidado);

		validaVinculoUnidadesFuncionais(seqCuidado);
		validaVinculoCuidadosPrescritos(seqCuidado);
		validaVinculoModeloBasicoPrescricao(seqCuidado);
		validaVinculoPrescricaoCuidados(seqCuidado);
		validaVinculoOrdemCuidados(seqCuidado);
		validaVinculoCadastroOrdemCuidados(seqCuidado);
		validaVinculoPrescricaoDeCuidados(seqCuidado);
		verificaPeriodoCriacao(mpmCuidadoUsual);

		removerCuidadosHospitalares(seqCuidado);

		this.getMpmCuidadoUsualDAO().remover(mpmCuidadoUsual);
		this.getMpmCuidadoUsualDAO().flush();

	}

	private void removerCuidadosHospitalares(Integer seqCuidado)
			throws ApplicationBusinessException {

		FatProcedHospInternos procedimentos = getFaturamentoFacade()
				.obterProcedimentoHospitalarInternoPorCuidadoUsual(seqCuidado);

		if (procedimentos != null) {
			try {
				this.getFaturamentoFacade().removerProcedimetoHospitalarInterno(procedimentos);
			} catch (PersistenceException e) {
				throw new ApplicationBusinessException(
						PesquisarCuidadoUsualONExceptionCode.ERRO_EXCLUSAO_PROCEDIMENTOS_HOSPITALARES_INTERNOS,
						e);
			}
		}
	}

	private void verificaPeriodoCriacao(MpmCuidadoUsual cuidadoUsual)
			throws ApplicationBusinessException {
		final String nome = "P_DIAS_PERM_DEL_MPM";
		BigDecimal vlrNumerico = getMpmCuidadoUsualDAO()
				.obterValorNumericoAghParametros(nome);

		if (vlrNumerico == null) {
			throw new ApplicationBusinessException(
					PesquisarCuidadoUsualONExceptionCode.ERRO_RECUPERACAO_PARAM_P_DIAS_PERM_MPM);
		}

		Long diferencaEmDias = this.diferencaEmDias(cuidadoUsual.getCriadoEm(),
				new Date());
		if (diferencaEmDias.intValue() > vlrNumerico.intValue()) {
			throw new ApplicationBusinessException(
					PesquisarCuidadoUsualONExceptionCode.REGISTRO_FORA_PERIODO_PERMITIDO_PARA_EXCLUSAO);
		}
	}

	/**
	 * Método que calcula a diferença em dias entre duas datas.
	 * 
	 * @param data1
	 *            , data2
	 * @return
	 */
	private Long diferencaEmDias(Date data1, Date data2) {

		long diferenca = data2.getTime() - data1.getTime();
		final int milisegundos = 1000;
		final int segundos = 60;
		final int minutos = 60;
		final int horas = 24;
		return diferenca / (milisegundos * segundos * minutos * horas);
	}

	private void validaVinculoUnidadesFuncionais(Integer seqCuidado)
			throws ApplicationBusinessException {
		List<AghUnidadesFuncionais> unidadesFuncionaisList = this
				.pesquisaUnidadesFuncionaisPorCuidadosUsuais(seqCuidado);

		if (unidadesFuncionaisList.size() > 0) {
			throw new ApplicationBusinessException(
					PesquisarCuidadoUsualONExceptionCode.MENSAGEM_CUIDADO_USUAL_POSSUI_UNIDADES_FUNCIONAIS);
		}
	}

	private List<AghUnidadesFuncionais> pesquisaUnidadesFuncionaisPorCuidadosUsuais(
			Integer seqCuidado) {

		MpmCuidadoUsual cuidado = getMpmCuidadoUsualDAO()
				.obterPorChavePrimaria(seqCuidado);

		List<AghUnidadesFuncionais> result = getMpmCuidadoUsualDAO()
				.pesquisaUnidadesFuncionaisPorCuidadosUsuais(cuidado);

		return result;
	}

	private void validaVinculoCuidadosPrescritos(Integer seqCuidado)
			throws ApplicationBusinessException {
		List<MpmMensPrcrCuidado> cuidadosPrescritosList = this
				.pesquisaCuidadosPrescritosPorCuidadosUsuais(seqCuidado);

		if (cuidadosPrescritosList.size() > 0) {
			throw new ApplicationBusinessException(
					PesquisarCuidadoUsualONExceptionCode.MENSAGEM_CUIDADO_USUAL_JA_PRESCRITO);
		}
	}

	private List<MpmMensPrcrCuidado> pesquisaCuidadosPrescritosPorCuidadosUsuais(
			Integer seqCuidado) {

		MpmCuidadoUsual cuidado = getMpmCuidadoUsualDAO()
				.obterPorChavePrimaria(seqCuidado);

		List<MpmMensPrcrCuidado> result = getMpmCuidadoUsualDAO()
				.pesquisaCuidadosPrescritosPorCuidadosUsuais(cuidado);

		return result;
	}

	private void validaVinculoModeloBasicoPrescricao(Integer seqCuidado)
			throws ApplicationBusinessException {
		List<MpmModeloBasicoCuidado> modeloBasicoPrescricaoList = this
				.pesquisaModeloBasicoPrescricaoPorCuidadosUsuais(seqCuidado);

		if (modeloBasicoPrescricaoList.size() > 0) {
			throw new ApplicationBusinessException(
					PesquisarCuidadoUsualONExceptionCode.MENSAGEM_CUIDADO_USUAL_JA_UTILIZADO_MODELO_BASICO_PRESCRICAO);
		}
	}

	private List<MpmModeloBasicoCuidado> pesquisaModeloBasicoPrescricaoPorCuidadosUsuais(
			Integer seqCuidado) {

		MpmCuidadoUsual cuidado = getMpmCuidadoUsualDAO()
				.obterPorChavePrimaria(seqCuidado);

		List<MpmModeloBasicoCuidado> result = getMpmCuidadoUsualDAO()
				.pesquisaModeloBasicoPrescricaoPorCuidadosUsuais(cuidado);

		return result;
	}

	private void validaVinculoPrescricaoCuidados(Integer seqCuidado)
			throws ApplicationBusinessException {
		List<MpmPrescricaoCuidado> modeloPrescricaoCuidadoList = this
				.pesquisaPrescricaoCuidadosPorCuidadosUsuais(seqCuidado);

		if (modeloPrescricaoCuidadoList.size() > 0) {
			throw new ApplicationBusinessException(
					PesquisarCuidadoUsualONExceptionCode.MENSAGEM_CUIDADO_USUAL_JA_UTILIZADO_PRESCRICAO_CUIDADOS);
		}
	}

	private List<MpmPrescricaoCuidado> pesquisaPrescricaoCuidadosPorCuidadosUsuais(
			Integer seqCuidado) {

		MpmCuidadoUsual cuidado = getMpmCuidadoUsualDAO()
				.obterPorChavePrimaria(seqCuidado);

		List<MpmPrescricaoCuidado> result = getMpmCuidadoUsualDAO()
				.pesquisaPrescricaoCuidadosPorCuidadosUsuais(cuidado);

		return result;
	}

	private void validaVinculoOrdemCuidados(Integer seqCuidado)
			throws ApplicationBusinessException {
		List<MpaUsoOrdCuidado> modeloPrescricaoCuidadoList = this
				.pesquisaOrdemCuidadosPorCuidadosUsuais(seqCuidado);

		if (modeloPrescricaoCuidadoList.size() > 0) {
			throw new ApplicationBusinessException(
					PesquisarCuidadoUsualONExceptionCode.MENSAGEM_CUIDADO_USUAL_JA_UTILIZADO_ORDEM_CUIDADOS);
		}
	}

	private List<MpaUsoOrdCuidado> pesquisaOrdemCuidadosPorCuidadosUsuais(
			Integer seqCuidado) {

		MpmCuidadoUsual cuidado = getMpmCuidadoUsualDAO()
				.obterPorChavePrimaria(seqCuidado);

		List<MpaUsoOrdCuidado> result = getMpmCuidadoUsualDAO()
				.pesquisaOrdemCuidadosPorCuidadosUsuais(cuidado);

		return result;
	}
	
	protected MpmCuidadoUsualUnfDAO getMpmCuidadoUsualUnfDAO() {
		return mpmCuidadoUsualUnfDAO;
	}
	
	private void validaVinculoCadastroOrdemCuidados(Integer seqCuidado)
			throws ApplicationBusinessException {
		List<MpaCadOrdCuidado> cadastroOrdemCuidadosList = this
				.pesquisaCadastroOrdemCuidadosPorCuidadosUsuais(seqCuidado);

		if (cadastroOrdemCuidadosList.size() > 0) {
			throw new ApplicationBusinessException(
					PesquisarCuidadoUsualONExceptionCode.MENSAGEM_CUIDADO_USUAL_JA_UTILIZADO_CADASTRO_ORDEM_CUIDADOS);
		}
	}

	private List<MpaCadOrdCuidado> pesquisaCadastroOrdemCuidadosPorCuidadosUsuais(
			Integer seqCuidado) {

		MpmCuidadoUsual cuidado = getMpmCuidadoUsualDAO()
				.obterPorChavePrimaria(seqCuidado);

		List<MpaCadOrdCuidado> result = getMpmCuidadoUsualDAO()
				.pesquisaCadastroOrdemCuidadosPorCuidadosUsuais(cuidado);

		return result;
	}

	private void validaVinculoPrescricaoDeCuidados(Integer seqCuidado)
			throws ApplicationBusinessException {
		List<MptPrescricaoCuidado> prescricaoDeCuidadosList = this
				.pesquisaPrescricaoDeCuidadosPorCuidadosUsuais(seqCuidado);

		if (prescricaoDeCuidadosList.size() > 0) {
			throw new ApplicationBusinessException(
					PesquisarCuidadoUsualONExceptionCode.MENSAGEM_CUIDADO_USUAL_JA_UTILIZADO_PRESCRICAO_CUIDADOS);
		}
	}

	private List<MptPrescricaoCuidado> pesquisaPrescricaoDeCuidadosPorCuidadosUsuais(
			Integer seqCuidado) {

		MpmCuidadoUsual cuidado = getMpmCuidadoUsualDAO()
				.obterPorChavePrimaria(seqCuidado);

		List<MptPrescricaoCuidado> result = getMpmCuidadoUsualDAO()
				.pesquisaPrescricaoDeCuidadosPorCuidadosUsuais(cuidado);

		return result;
	}

	// getters & setters

	protected MpmServidorUnidFuncionalDAO getMpmServidorUnidFuncionaisDAO() {
		return mpmServidorUnidFuncionalDAO;
	}

	protected MpmCuidadoUsualDAO getMpmCuidadoUsualDAO() {
		return mpmCuidadoUsualDAO;
	}

	public List<AghUnidadesFuncionais> getListaUnidadesFuncionais(
			String paramString) {
		return getAghuFacade().pesquisarPorDescricaoCodigoAtivaAssociada(paramString);
	}

	public List<AghUnidadesFuncionais> getListaUnidadesFuncionais(
			RapServidores servidor) throws ApplicationBusinessException {
		if (servidor == null || servidor.getId() == null) {
			throw new ApplicationBusinessException(
					PesquisarCuidadoUsualONExceptionCode.MENSAGEM_SERVIDOR_INVALIDO);
		}
		MpmServidorUnidFuncionalDAO dao = getMpmServidorUnidFuncionaisDAO();
		List<AghUnidadesFuncionais> result = dao
				.pesquisarUnidadesFuncionaisPorServidor(servidor);
		if (result == null) {
			result = new ArrayList<AghUnidadesFuncionais>();
		}
		return result;
	}

	public Long pesquisarCuidadoUsualCount(Integer codigo, String descricao,
			DominioSituacao situacao, AghUnidadesFuncionais unidadeFuncional) {

		return this.getMpmCuidadoUsualDAO().pesquisarCuidadoUsualCount(codigo,
				descricao, situacao, unidadeFuncional);
	}

	public List<MpmCuidadoUsual> pesquisarCuidadoUsual(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Integer codigo, String descricao, DominioSituacao situacao,
			AghUnidadesFuncionais unidadeFuncional) {

		return this.getMpmCuidadoUsualDAO().pesquisarCuidadoUsual(firstResult,
				maxResult, orderProperty, asc, codigo, descricao, situacao,
				unidadeFuncional);
	}
		
	protected IFaturamentoFacade getFaturamentoFacade() {
		return this.faturamentoFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
	public List<MpmCuidadoUsualUnf> pesquisarCuidadoUsualUnf(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AghUnidadesFuncionais unidadeFuncional) {
		return this.getMpmCuidadoUsualUnfDAO().pesquisarCuidadoUsualUnf(
				firstResult, maxResult, orderProperty, asc, unidadeFuncional);
	}

	public Integer pesquisarCuidadoUsualUnfCount(
			AghUnidadesFuncionais unidadeFuncional) {
		return this.getMpmCuidadoUsualUnfDAO().pesquisarCuidadoUsualUnfCount(
				unidadeFuncional);
	}
	
}
