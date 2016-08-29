package br.gov.mec.aghu.estoque.business;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioClassifABC;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.dao.SceAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceConsumoTotalMateriaisDAO;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceEstoqueGeralDAO;
import br.gov.mec.aghu.estoque.dao.SceHistoricoProblemaMaterialDAO;
import br.gov.mec.aghu.estoque.dao.SceItemRecebProvisorioDAO;
import br.gov.mec.aghu.estoque.vo.ConsumoPonderadoVO;
import br.gov.mec.aghu.estoque.vo.EstatisticaEstoqueAlmoxarifadoVO;
import br.gov.mec.aghu.estoque.vo.QtdeRpVO;
import br.gov.mec.aghu.estoque.vo.QtdeRpVO.NotaRecebimentoProvisorio;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueGeral;
import br.gov.mec.aghu.model.SceEstoqueGeralId;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings({ "PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength" })
@Stateless
public class SceEstoqueAlmoxarifadoRN extends BaseBusiness {

	private static final String _0_0 = "0,0";

	@EJB
	private SceEstoqueGeralRN sceEstoqueGeralRN;

	private static final Log LOG = LogFactory.getLog(SceEstoqueAlmoxarifadoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private SceConsumoTotalMateriaisDAO sceConsumoTotalMateriaisDAO;

	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IFarmaciaApoioFacade farmaciaApoioFacade;

	@Inject
	private SceItemRecebProvisorioDAO sceItemRecebProvisorioDAO;

	@Inject
	private SceAlmoxarifadoDAO sceAlmoxarifadoDAO;

	@EJB
	private IFarmaciaFacade farmaciaFacade;

	@Inject
	private SceEstoqueGeralDAO sceEstoqueGeralDAO;

	@Inject
	private SceHistoricoProblemaMaterialDAO sceHistoricoProblemaMaterialDAO;

	@Inject
	private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6400495527570282196L;

	public enum SceEstoqueAlmoxarifadoRNExceptionCode implements BusinessExceptionCode {
		SCE_00630_2, SCE_00447, SCE_00891, SCE_00298, SCE_00290, SCE_00881, SCE_00443, SCE_00444, SCE_00279, SCE_00281, SCE_00384, SCE_00385, SCE_00386, SCE_00387, SCE_00725, MATERIAL_NAO_DESATIVADO, SCE_00018, ERRO_ESTOQUE_ALMOXARIFADO_CHAVE_UNICA, MENSAGEM_MATERIAL_SALDO_ALMOXARIFADO_DESATIVADO, SCE_00292, SCE_00280;
	}

	/**
	 * Atualizar SceEstoqueAlmoxarifado
	 * 
	 * @param SceEstoqueAlmoxarifado
	 * @throws BaseException
	 */
	public void atualizar(SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado, String nomeMicrocomputador, Boolean flush) throws BaseException {

		SceEstoqueAlmoxarifado oldEstAlmoxarifado = this.getSceEstoqueAlmoxarifadoDAO().obterOriginal(sceEstoqueAlmoxarifado);
		this.preAtualizar(sceEstoqueAlmoxarifado, oldEstAlmoxarifado);
		
		this.getSceEstoqueAlmoxarifadoDAO().atualizar(sceEstoqueAlmoxarifado);

		if (flush) {
			this.getSceEstoqueAlmoxarifadoDAO().flush();
		}

		this.posAtualizar(sceEstoqueAlmoxarifado, oldEstAlmoxarifado, nomeMicrocomputador);
	}

	/**
	 * 
	 * SCEP_ENFORCE_EAL_RULES (UPDATE)
	 * 
	 * @param sceEstoqueAlmoxarifado
	 * @throws BaseException
	 */
	protected void posAtualizar(SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado, SceEstoqueAlmoxarifado oldEstAlmoxarifado,
			String nomeMicrocomputador) throws BaseException {

		this.atualizaIndRevisaoCadastro(sceEstoqueAlmoxarifado, oldEstAlmoxarifado, nomeMicrocomputador); //RN1
		this.validaSaldoEstoqueMaterial(sceEstoqueAlmoxarifado, oldEstAlmoxarifado);//RN2

	}

	private void validaSaldoEstoqueMaterial(SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado, SceEstoqueAlmoxarifado oldEstAlmoxarifado)
			throws ApplicationBusinessException {

		if (sceEstoqueAlmoxarifado != null && oldEstAlmoxarifado != null) {
			if (oldEstAlmoxarifado.getIndSituacao().equals(DominioSituacao.A)
					&& sceEstoqueAlmoxarifado.getIndSituacao().equals(DominioSituacao.I)) {

				Integer saldo = this.getSceEstoqueAlmoxarifadoDAO().pesquisarSaldoMaterialEstoqueAlmoxarifado(sceEstoqueAlmoxarifado);

				if (saldo != null && saldo > 0) {

					throw new ApplicationBusinessException(SceEstoqueAlmoxarifadoRNExceptionCode.MATERIAL_NAO_DESATIVADO);

				}

			}
		}

	}

	/*
	 * Atualiza IND_REVISAO_CADASTRO AFA_MEDICAMENTOS
	 */
	private void atualizaIndRevisaoCadastro(SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado, SceEstoqueAlmoxarifado oldEstAlmoxarifado,
			String nomeMicrocomputador) throws IllegalStateException, BaseException {
		if (sceEstoqueAlmoxarifado != null && oldEstAlmoxarifado != null) {
			if (sceEstoqueAlmoxarifado.getIndEstocavel().equals(Boolean.TRUE)
					&& oldEstAlmoxarifado.getIndSituacao().equals(DominioSituacao.A)
					&& sceEstoqueAlmoxarifado.getIndSituacao().equals(DominioSituacao.I)) {

				AghParametros parametro = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_GR_MAT_MEDIC);

				if (sceEstoqueAlmoxarifado.getMaterial() != null) {

					if ((parametro.getVlrNumerico().intValue() == sceEstoqueAlmoxarifado.getMaterial().getCodigo())) {

						Integer saldo = this.getSceEstoqueAlmoxarifadoDAO().pesquisarQtdeSaldoMaterial(sceEstoqueAlmoxarifado); //sum(qtde_disponivel + qtde_bloqueada)

						if (saldo == 0) {

							AfaMedicamento afaMedicamento = this.getFarmaciaFacade().obterMedicamento(
									sceEstoqueAlmoxarifado.getMaterial().getCodigo());
							afaMedicamento.setIndRevisaoCadastro(Boolean.TRUE);
							this.getFarmaciaApoioFacade().efetuarAlteracao(afaMedicamento, nomeMicrocomputador, new Date());

						}

					}

				} else {
					//Material não cadastrado ou inativo
					throw new ApplicationBusinessException(SceEstoqueAlmoxarifadoRNExceptionCode.SCE_00881);

				}

			}
		}
	}

	protected IFarmaciaFacade getFarmaciaFacade() {
		return farmaciaFacade;
	}

	protected IFarmaciaApoioFacade getFarmaciaApoioFacade() {
		return farmaciaApoioFacade;
	}

	/**
	 * ORADB SCET_EAL_BRU (UPDATE) Pré-inserir SceMovimentoMaterial
	 * 
	 * @param sceMovimentoMaterial
	 * @throws BaseException
	 */
	protected void preAtualizar(SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado, SceEstoqueAlmoxarifado oldEstAlmoxarifado)
			throws ApplicationBusinessException {

		if (sceEstoqueAlmoxarifado != null && oldEstAlmoxarifado != null) {

			/* Não estava corretamente implementada */
			verificaEstoqueAlomxarifadoExistente(sceEstoqueAlmoxarifado);//função inserida de acordo com a tarefa cadastrada no redmine #13915
			/*
			 * if((!sceEstoqueAlmoxarifado.getFornecedor().getNumero().equals(
			 * oldEstAlmoxarifado.getFornecedor().getNumero())) ||
			 * (!sceEstoqueAlmoxarifado
			 * .getMaterial().getCodigo().equals(oldEstAlmoxarifado
			 * .getMaterial().getCodigo()))){
			 * verificaEstoqueAlomxarifadoExistente(sceEstoqueAlmoxarifado);
			 * //função inserida de acordo com a tarefa cadastrada no redmine
			 * #13915 }
			 */
			this.validaSceEstoqueAlmoxarifado(sceEstoqueAlmoxarifado, oldEstAlmoxarifado); // RN1
			this.atualizaDataUsuarioAlteracao(sceEstoqueAlmoxarifado);//RN2
			
			if (!Objects.equals(sceEstoqueAlmoxarifado.getIndSituacao(), oldEstAlmoxarifado.getIndSituacao())
					&& sceEstoqueAlmoxarifado.getIndSituacao().equals(DominioSituacao.I)) {
				this.verificaSaldoAlmoxarifado(sceEstoqueAlmoxarifado, oldEstAlmoxarifado);
				this.atualizaDataUsuarioDesativacao(sceEstoqueAlmoxarifado);//RN3
			}
			
			this.validaTempoRepMaterialEstocavel(sceEstoqueAlmoxarifado, oldEstAlmoxarifado);//RN4
			this.validaAlmoxarifadoAtivo(sceEstoqueAlmoxarifado, oldEstAlmoxarifado);//RN5
			if (!sceEstoqueAlmoxarifado.getFornecedor().getNumero().equals(oldEstAlmoxarifado.getFornecedor().getNumero())) {
				this.validaFornecedorAtivo(sceEstoqueAlmoxarifado);//RN6
			}

			if (!sceEstoqueAlmoxarifado.getMaterial().equals(oldEstAlmoxarifado.getMaterial())) {
				this.validaMaterialAtivo(sceEstoqueAlmoxarifado.getMaterial());//RN7
			}

			if (sceEstoqueAlmoxarifado.getSolicitacaoCompra() != null
					&& !sceEstoqueAlmoxarifado.getSolicitacaoCompra().equals(oldEstAlmoxarifado.getSolicitacaoCompra())) {
				this.atualizarSolicCompScContr(sceEstoqueAlmoxarifado.getSolicitacaoCompra());
			}
			if (!sceEstoqueAlmoxarifado.getUnidadeMedida().equals(oldEstAlmoxarifado.getUnidadeMedida())) {
				this.validaUnidadeMedidaAtiva(sceEstoqueAlmoxarifado);//RN10
			}
			if (!sceEstoqueAlmoxarifado.getUnidadeMedida().equals(oldEstAlmoxarifado.getUnidadeMedida())) {
				this.verUnidadeMedidaMaterial(sceEstoqueAlmoxarifado);//RN10
			}

			this.validaQuantidades(sceEstoqueAlmoxarifado, oldEstAlmoxarifado);//RN11
			this.atualizaQtdePontoPedido(sceEstoqueAlmoxarifado, oldEstAlmoxarifado);//RN12
		}

	}

	private void verificaSaldoAlmoxarifado(SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado, SceEstoqueAlmoxarifado oldEstAlmoxarifado)
			throws ApplicationBusinessException {
		final Boolean isExisteSaldo = this.existeSaldoEstoqueAlmoxarifado(sceEstoqueAlmoxarifado.getMaterial().getCodigo(),
				sceEstoqueAlmoxarifado.getAlmoxarifado().getSeq(), sceEstoqueAlmoxarifado.getFornecedor().getNumero());

		if (isExisteSaldo) {
			sceEstoqueAlmoxarifado.setIndSituacao(oldEstAlmoxarifado.getIndSituacao());
			throw new ApplicationBusinessException(SceEstoqueAlmoxarifadoRNExceptionCode.MENSAGEM_MATERIAL_SALDO_ALMOXARIFADO_DESATIVADO);
		}

	}

	/**
	 * TODO: ORADB SCEK_EAL_RN.RN_EALP_VER_SC_CONTR Atualiza qtde aprovada e
	 * qtde entregue conforme a qtde solicitada e entregue od ítem da AF Esta
	 * procedure será implementada posteriormente, devido a migração de triggers
	 * de update.
	 * 
	 * @param scoSolicitacaoCompra
	 */
	private void atualizarSolicCompScContr(ScoSolicitacaoDeCompra scoSolicitacaoCompra) {
		// TODO Auto-generated method stub

	}

	/**
	 * RN12
	 * 
	 * @param sceEstoqueAlmoxarifado
	 * @param oldEstAlmoxarifado
	 * @throws ApplicationBusinessException
	 */
	public void atualizaQtdePontoPedido(SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado, SceEstoqueAlmoxarifado oldEstAlmoxarifado)
			throws ApplicationBusinessException {
		if (!sceEstoqueAlmoxarifado.getQtdePontoPedido().equals(oldEstAlmoxarifado.getQtdePontoPedido())) {
			//v_qtde_saldo := :new.qtde_disponivel + :new.qtde_bloqueada;
			//Integer qtdeSaldo = sceEstoqueAlmoxarifado.getQtdeDisponivel() + sceEstoqueAlmoxarifado.getQtdeBloqueada();
			//Integer vItemProgr = 0;
			Integer vOldTempoReposicao = 0;
			Integer vConsumoDia = 0;

			/**
			 * Realiza consulta para verificar se o Material tem Programação de
			 * Entrega de AF não Assinada. Nesse caso revê e atualiza a
			 * Programação.
			 */
			//vItemProgr = this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().pesquisaProgramacaoEntregaAfAssinada(sceEstoqueAlmoxarifado.getMaterial());
			//Este código está comentado na TRIGGER "AGH".SCET_EAL_BRU
			//if(vItemProgr.intValue() > 0){				
			//  if v_item_progr > 0     -- Tem Programação Não Assinada ainda.    Deve reprogramar.
			//         then
			//          -- Obs.  Bloqueada apenas para não rodar no Fechamento Mesal.  Reabrir após Alteração e Teste
			//             null;  -- SCOK_PEA_RN.RN_PEAP_PROG_ENTG_AF(null, null, :new.mat_codigo, :new.qtde_ponto_pedido, :new.tempo_reposicao, v_qtde_saldo,'N');
			//  end if;				
			//}

			if (sceEstoqueAlmoxarifado != null && oldEstAlmoxarifado != null && sceEstoqueAlmoxarifado.getTempoReposicao() != null
					&& oldEstAlmoxarifado.getTempoReposicao() != null
					&& !sceEstoqueAlmoxarifado.getTempoReposicao().equals(oldEstAlmoxarifado.getTempoReposicao())
					&& oldEstAlmoxarifado.getTempoReposicao().intValue() > 0 && sceEstoqueAlmoxarifado.getTempoReposicao().intValue() > 0
					&& sceEstoqueAlmoxarifado.getIndEstocavel().equals(Boolean.TRUE)) {

				if (sceEstoqueAlmoxarifado.getIndPontoPedidoCalc().equals(Boolean.FALSE)) {

					if (oldEstAlmoxarifado.getTempoReposicao() == null || oldEstAlmoxarifado.getTempoReposicao().intValue() == 0) {
						vOldTempoReposicao = 1;

					} else {
						vOldTempoReposicao = oldEstAlmoxarifado.getTempoReposicao();
					}

					vConsumoDia = sceEstoqueAlmoxarifado.getQtdePontoPedido().intValue() / vOldTempoReposicao.intValue();

					sceEstoqueAlmoxarifado.setQtdePontoPedido(sceEstoqueAlmoxarifado.getTempoReposicao() * vConsumoDia);

					if (sceEstoqueAlmoxarifado.getQtdePontoPedido().intValue() < 1) {
						sceEstoqueAlmoxarifado.setQtdePontoPedido(1);
					}

				}

			}

		}

	}

	/*
	 * Verifica as quantidades do estoque ORADB SCEK_EAL_RN.RN_EALP_VER_QTDES
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void validaQuantidades(SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado, SceEstoqueAlmoxarifado oldEstAlmoxarifado)
			throws ApplicationBusinessException {

		if (!sceEstoqueAlmoxarifado.getQtdeDisponivel().equals(oldEstAlmoxarifado.getQtdeDisponivel())) {
			if (sceEstoqueAlmoxarifado.getQtdeDisponivel() == null || sceEstoqueAlmoxarifado.getQtdeDisponivel().intValue() < 0) {
				//quantidade  disponivel deve ser maior ou igual a zero
				throw new ApplicationBusinessException(SceEstoqueAlmoxarifadoRNExceptionCode.SCE_00385, sceEstoqueAlmoxarifado
						.getMaterial().getCodigo());
			}
		}
		if (!sceEstoqueAlmoxarifado.getQtdeBloqueada().equals(oldEstAlmoxarifado.getQtdeBloqueada())) {
			if (sceEstoqueAlmoxarifado.getQtdeBloqueada() == null || sceEstoqueAlmoxarifado.getQtdeBloqueada().intValue() < 0) {
				//quantidade  bloqueada  deve ser maior ou igual a zero
				throw new ApplicationBusinessException(SceEstoqueAlmoxarifadoRNExceptionCode.SCE_00384, sceEstoqueAlmoxarifado
						.getMaterial().getCodigo(), sceEstoqueAlmoxarifado.getQtdeBloqueada());
			}
		}
		if (!sceEstoqueAlmoxarifado.getQtdeEmUso().equals(oldEstAlmoxarifado.getQtdeEmUso())) {
			if (sceEstoqueAlmoxarifado.getQtdeEmUso() == null || sceEstoqueAlmoxarifado.getQtdeEmUso().intValue() < 0) {
				//quantidade em uso  deve ser maior ou igual a zero
				throw new ApplicationBusinessException(SceEstoqueAlmoxarifadoRNExceptionCode.SCE_00386, sceEstoqueAlmoxarifado
						.getMaterial().getCodigo());

			}
		}
		if (!sceEstoqueAlmoxarifado.getQtdePontoPedido().equals(oldEstAlmoxarifado.getQtdePontoPedido())) {
			if (sceEstoqueAlmoxarifado.getQtdePontoPedido() == null || sceEstoqueAlmoxarifado.getQtdePontoPedido().intValue() < 0) {
				//quantidade ponto pedido deve ser maior ou igual a zero
				throw new ApplicationBusinessException(SceEstoqueAlmoxarifadoRNExceptionCode.SCE_00387, sceEstoqueAlmoxarifado
						.getMaterial().getCodigo());

			}
		}
		if (!sceEstoqueAlmoxarifado.getQtdeEstqMin().equals(oldEstAlmoxarifado.getQtdeEstqMin())) {
			if (sceEstoqueAlmoxarifado.getQtdeEstqMin() == null || sceEstoqueAlmoxarifado.getQtdeEstqMin().intValue() < 0) {
				//quantidade estq_min  deve ser maior ou igual a zero
				throw new ApplicationBusinessException(SceEstoqueAlmoxarifadoRNExceptionCode.SCE_00725, sceEstoqueAlmoxarifado
						.getMaterial().getCodigo());

			}
		}

	}

	/*
	 * Verifica se unidade medida está ativa ORADB
	 * SCEK_EAL_RN.RN_EALP_VER_UMD_ATIV
	 */
	public void validaUnidadeMedidaAtiva(SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado) throws ApplicationBusinessException {

		if (sceEstoqueAlmoxarifado.getUnidadeMedida().getSituacao().equals(DominioSituacao.I)) {
			//Unidade de Medida  não cadastrada ou Inativa
			throw new ApplicationBusinessException(SceEstoqueAlmoxarifadoRNExceptionCode.SCE_00279);
		}

	}

	/**
	 * ORADB SCEK_EAL_RN.RN_EALP_VER_UMD_MAT Verifica se unidade medida é igual
	 * a do cadastro de Material
	 * 
	 * @param sceEstoqueAlmoxarifado
	 * @param oldEstAlmoxarifado
	 * @throws ApplicationBusinessException
	 */
	public void verUnidadeMedidaMaterial(SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado) throws ApplicationBusinessException {
		if (!sceEstoqueAlmoxarifado.getUnidadeMedida().equals(sceEstoqueAlmoxarifado.getMaterial().getUnidadeMedida())) {
			// Unidade de Medida não é igual a do cadastro de Material
			throw new ApplicationBusinessException(SceEstoqueAlmoxarifadoRNExceptionCode.SCE_00281);
		}
	}

	/*
	 * Verifica se o material é estocável (caso contrário, é de consumo direto)
	 * ORADB SCEK_EAL_RN.RN_EALP_VER_IND_ESTC
	 */
	@SuppressWarnings("ucd")
	public void validaMaterialEstocavel(SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado) throws ApplicationBusinessException {

		final Integer codigoMaterial = sceEstoqueAlmoxarifado.getMaterial().getCodigo();
		final Short seqAlmoxarifado = sceEstoqueAlmoxarifado.getAlmoxarifado().getSeq();
		final Boolean indEstocavel = sceEstoqueAlmoxarifado.getIndEstocavel();

		final Boolean isExisteEstoqueAlmoxPorMaterialAlmoxEstocavel = this.getComprasFacade()
				.existeMaterialEstocavelPorAlmoxarifadoCentral(codigoMaterial, seqAlmoxarifado, indEstocavel);

		// Se NÃO encontrou
		if (!isExisteEstoqueAlmoxPorMaterialAlmoxEstocavel) {
			if (sceEstoqueAlmoxarifado.getAlmoxarifado().getIndCentral().equals(Boolean.TRUE)) {
				if (sceEstoqueAlmoxarifado.getMaterial().getIndEstocavel().equals(DominioSimNao.S)) {
					//Material não é estocável no Cadastro de materiais
					throw new ApplicationBusinessException(SceEstoqueAlmoxarifadoRNExceptionCode.SCE_00443);
				} else {
					//Material não é de consumo direto no Cadastro de materiais
					throw new ApplicationBusinessException(SceEstoqueAlmoxarifadoRNExceptionCode.SCE_00444);
				}
			}
		}

	}

	/*
	 * ORADB SCEK_EAL_RN.RN_EALP_VER_MAT_ATIV
	 */
	public void validaMaterialAtivo(ScoMaterial scoMaterial) throws ApplicationBusinessException {

		if (scoMaterial == null || !DominioSituacao.A.equals(scoMaterial.getIndSituacao())) {
			throw new ApplicationBusinessException(SceEstoqueAlmoxarifadoRNExceptionCode.SCE_00881);
		}

	}

	/*
	 * ORADB SCEK_EAL_RN.RN_EALP_VER_FRN_ATIV
	 */
	public void validaFornecedorAtivo(SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado) throws ApplicationBusinessException {

		if (sceEstoqueAlmoxarifado.getFornecedor() == null
				|| !sceEstoqueAlmoxarifado.getFornecedor().getSituacao().equals(DominioSituacao.A)) {
			throw new ApplicationBusinessException(SceEstoqueAlmoxarifadoRNExceptionCode.SCE_00290);
		}

	}

	/*
	 * ORADB SCEK_EAL_RN.RN_EALP_VER_ALM_ATIV
	 */
	public void validaAlmoxarifadoAtivo(SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado, SceEstoqueAlmoxarifado oldEstAlmoxarifado)
			throws ApplicationBusinessException {

		if (!sceEstoqueAlmoxarifado.getAlmoxarifado().equals(oldEstAlmoxarifado.getAlmoxarifado())) {

			if (!sceEstoqueAlmoxarifado.getAlmoxarifado().getIndSituacao().equals(DominioSituacao.A)) {

				throw new ApplicationBusinessException(SceEstoqueAlmoxarifadoRNExceptionCode.SCE_00298);

			}

		}

	}

	/*
	 * Testa se tempo de reposicao foi modificado e se é estocavel.
	 */
	public void validaTempoRepMaterialEstocavel(SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado, SceEstoqueAlmoxarifado oldEstAlmoxarifado)
			throws ApplicationBusinessException {

		if ((sceEstoqueAlmoxarifado.getTempoReposicao() == null || sceEstoqueAlmoxarifado.getTempoReposicao() == 0)
				&& sceEstoqueAlmoxarifado.getTempoReposicao() != null
				&& !sceEstoqueAlmoxarifado.getTempoReposicao().equals(oldEstAlmoxarifado.getTempoReposicao())
				&& sceEstoqueAlmoxarifado.getIndEstocavel().equals(Boolean.TRUE)) {

			//“Material Estocável deve ter Tempo de Reposição válido.’
			throw new ApplicationBusinessException(SceEstoqueAlmoxarifadoRNExceptionCode.SCE_00891);

		}

	}

	/*
	 * Atualiza data e usuário desativação RN3: SCEK_EAL_RN.RN_EALP_ATU_DESTIV
	 */
	public void atualizaDataUsuarioDesativacao(SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		sceEstoqueAlmoxarifado.setServidorDesativado(servidorLogado);
		sceEstoqueAlmoxarifado.setDtDesativacao(new Date());

	}

	/*
	 * Atualiza data e usuário RN3: SCEK_EAL_RN.RN_EALP_ATU_DESTIV
	 */
	public void atualizaDataUsuario(SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		sceEstoqueAlmoxarifado.setServidor(servidorLogado);
		sceEstoqueAlmoxarifado.setDtGeracao(new Date());

	}

	/*
	 * Atualiza data e usuário alteração RN2: SCEK_EAL_RN.RN_EALP_ATU_ALT
	 */
	public void atualizaDataUsuarioAlteracao(SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		sceEstoqueAlmoxarifado.setServidorAlterado(servidorLogado);
		sceEstoqueAlmoxarifado.setDtAlteracao(new Date());
	}

	public void validaSceEstoqueAlmoxarifado(SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado, SceEstoqueAlmoxarifado oldEstAlmoxarifado)
			throws ApplicationBusinessException {

		if ((sceEstoqueAlmoxarifado.getIndSituacao() != null && sceEstoqueAlmoxarifado.getIndSituacao().equals(DominioSituacao.I))
				&& (oldEstAlmoxarifado.getIndSituacao() != null && oldEstAlmoxarifado.getIndSituacao().equals(DominioSituacao.I))) {

			if (this.validaAlteracaoEstoque(sceEstoqueAlmoxarifado, oldEstAlmoxarifado)) {
				//SCE-00447: “Material está inativo no almoxarifado. Não permite alteração”
				throw new ApplicationBusinessException(SceEstoqueAlmoxarifadoRNExceptionCode.SCE_00447);
			}

		}

	}

	/*
	 * SCEK_EAL_RN.RN_EALP_ATU_QTDES
	 */
	public void atualizaQtdesSceEstoqueAlmoxarifado(SceEstoqueAlmoxarifado estoqueAlmox) {
		if (estoqueAlmox.getQtdeDisponivel() == null) {
			estoqueAlmox.setQtdeDisponivel(0);
		}
		if (estoqueAlmox.getQtdeBloqueada() == null) {
			estoqueAlmox.setQtdeBloqueada(0);
		}
		if (estoqueAlmox.getQtdeEstqMin() == null) {
			estoqueAlmox.setQtdeEstqMin(0);
		}
		if (estoqueAlmox.getQtdeEstqMax() == null) {
			estoqueAlmox.setQtdeEstqMax(0);
		}
		if (estoqueAlmox.getQtdePontoPedido() == null) {
			estoqueAlmox.setQtdePontoPedido(0);
		}
		if (estoqueAlmox.getQtdeBloqEntrTransf() == null) {
			estoqueAlmox.setQtdeBloqEntrTransf(0);
		}
		if (estoqueAlmox.getQtdeEmUso() == null) {
			estoqueAlmox.setQtdeEmUso(0);
		}
		if (estoqueAlmox.getQtdeBloqConsumo() == null) {
			estoqueAlmox.setQtdeBloqConsumo(0);
		}
		if (estoqueAlmox.getQtdeBloqDispensacao() == null) {
			estoqueAlmox.setQtdeBloqDispensacao(0);
		}
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public Boolean validaAlteracaoEstoque(SceEstoqueAlmoxarifado newEstoque, SceEstoqueAlmoxarifado oldEstoque)
			throws ApplicationBusinessException {
		Boolean retorno = false;

		if (newEstoque.getUnidadeMedida() != null && !newEstoque.getUnidadeMedida().equals(oldEstoque.getUnidadeMedida())) {
			retorno = true;
		}
		if (newEstoque.getIndEstocavel() != null && !newEstoque.getIndEstocavel().equals(oldEstoque.getIndEstocavel())) {
			retorno = true;
		}
		if (newEstoque.getIndEstqMinCalc() != null && !newEstoque.getIndEstqMinCalc().equals(oldEstoque.getIndEstqMinCalc())) {
			retorno = true;
		}
		if (newEstoque.getIndEstqMinCalc() != null && !newEstoque.getIndEstqMinCalc().equals(oldEstoque.getIndEstqMinCalc())) {
			retorno = true;
		}
		if (newEstoque.getIndPontoPedidoCalc() != null && !newEstoque.getIndPontoPedidoCalc().equals(oldEstoque.getIndPontoPedidoCalc())) {
			retorno = true;
		}
		if (newEstoque.getQtdeEstqMin() != null && !newEstoque.getQtdeEstqMin().equals(oldEstoque.getQtdeEstqMin())) {
			retorno = true;
		}
		if (newEstoque.getQtdeEstqMax() != null && !newEstoque.getQtdeEstqMax().equals(oldEstoque.getQtdeEstqMax())) {
			retorno = true;
		}
		if (newEstoque.getTempoReposicao() != null && !newEstoque.getTempoReposicao().equals(oldEstoque.getTempoReposicao())) {
			retorno = true;
		}
		if (newEstoque.getSolicitacaoCompra() != null && !newEstoque.getSolicitacaoCompra().equals(oldEstoque.getSolicitacaoCompra())) {
			retorno = true;
		}
		if (newEstoque.getEndereco() != null && !newEstoque.getEndereco().equals(oldEstoque.getEndereco())) {
			retorno = true;
		}

		return retorno;

	}

	public void inserir(SceEstoqueAlmoxarifado estoqueAlmox) throws BaseException {
		preInserir(estoqueAlmox);
		getSceEstoqueAlmoxarifadoDAO().persistir(estoqueAlmox);
	}

	/**
	 * ORADB TRIGGER SCET_EAL_BRI
	 * 
	 */
	private void preInserir(SceEstoqueAlmoxarifado estoqueAlmox) throws BaseException {

		AghParametros paramCompetencia = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COMPETENCIA);
		Date dtCompetencia = paramCompetencia.getVlrData();
		verificaEstoqueAlomxarifadoExistente(estoqueAlmox); //função inserida de acordo com a tarefa cadastrada no redmine #13280

		ScoMaterial material = getComprasFacade().obterScoMaterial(estoqueAlmox.getMaterial().getCodigo());
		estoqueAlmox.setMaterial(material);
		//estoqueAlmox.setUnidadeMedida(material.getUnidadeMedida());

		atualizaDataUsuarioAlteracao(estoqueAlmox);
		atualizaDataUsuario(estoqueAlmox);
		atualizaQtdesSceEstoqueAlmoxarifado(estoqueAlmox);
		if (estoqueAlmox.getIndEstocavel()) {
			atualizaTempoReposicao(estoqueAlmox, dtCompetencia);
		}
		validaMaterialAtivo(estoqueAlmox.getMaterial());
		validaFornecedorAtivo(estoqueAlmox);
		//validaMaterialEstocavel(estoqueAlmox);  // Esta validação não funciona no AGH: Necessita revisão!
		validaMaterialAtivo(estoqueAlmox.getMaterial());
		atualizarSolicCompScContr(estoqueAlmox.getSolicitacaoCompra());
		atualizaUnidadeMedida(estoqueAlmox);
		validaUnidadeMedidaAtiva(estoqueAlmox);
		verUnidadeMedidaMaterial(estoqueAlmox);

		if (estoqueAlmox.getFornecedor().getNumero() != 1) {
			if (getSceEstoqueGeralDAO().countSceEstoqueGeralMaterialFornecedorDataCompetencia(estoqueAlmox, dtCompetencia) == 0) {
				SceEstoqueGeral sceEstoqueGeral = new SceEstoqueGeral();
				sceEstoqueGeral.setId(new SceEstoqueGeralId());

				sceEstoqueGeral.setMaterial(estoqueAlmox.getMaterial());
				sceEstoqueGeral.setCustoMedioPonderado(BigDecimal.ZERO);
				sceEstoqueGeral.setResiduo((double) 0);
				sceEstoqueGeral.setValor((double) 0);
				sceEstoqueGeral.setUnidadeMedida(estoqueAlmox.getUnidadeMedida());
				sceEstoqueGeral.setClassificacaoAbc(null);
				sceEstoqueGeral.setSubClassificacaoAbc(null);
				sceEstoqueGeral.setQtde(0);
				sceEstoqueGeral.setFornecedor(estoqueAlmox.getFornecedor());
				getSceEstoqueGeralRN().inserir(sceEstoqueGeral);
			}
		}

	}

	/**
	 * ORADB SCEK_EAL_RN.RN_EALP_ATU_UMD Atualiza Unid medida a partir do
	 * cadastro do material
	 * 
	 * @param estoqueAlmox
	 * 
	 */
	public void atualizaUnidadeMedida(SceEstoqueAlmoxarifado estoqueAlmox) {
		estoqueAlmox.setUnidadeMedida(estoqueAlmox.getMaterial().getUnidadeMedida());
	}

	/**
	 * ORADB SCEK_EAL_RN.RN_EALP_ATU_TEMP_REP Atualiza Tempo de Reposição com
	 * valor default do almoxarifado
	 * 
	 * @param estoqueAlmox
	 * @param dtCompetencia
	 * @throws ApplicationBusinessException
	 */

	public void atualizaTempoReposicao(SceEstoqueAlmoxarifado estoqueAlmox, Date dtCompetencia) throws ApplicationBusinessException {

		SceEstoqueGeral estoqueGeral = getSceEstoqueGeralDAO().obterSceEstoqueGeralPorMaterialDataCompetencia(estoqueAlmox.getMaterial(),
				dtCompetencia);
		if (estoqueGeral != null && estoqueGeral.getClassificacaoAbc() != null) {
			if (estoqueGeral.getClassificacaoAbc().equals(DominioClassifABC.A)) {
				estoqueAlmox.setTempoReposicao(estoqueAlmox.getAlmoxarifado().getTempoReposicaoClassA());
			} else if (estoqueGeral.getClassificacaoAbc().equals(DominioClassifABC.B)) {
				estoqueAlmox.setTempoReposicao(estoqueAlmox.getAlmoxarifado().getTempoReposicaoClassB());
			} else {
				estoqueAlmox.setTempoReposicao(estoqueAlmox.getAlmoxarifado().getTempoReposicaoClassC());
			}
		}
	}

	/**
	 * ORADB PROCEDURE SCEK_EAL_RN.RN_EALP_ATU_SIT_ESTQ Desativa o material no
	 * estoque quando saldo é zero
	 * 
	 * @param material
	 */
	public void desativarEstoqueAlmoxarifadoPorMaterialSemSaldo(ScoMaterial material, String nomeMicrocomputador) throws BaseException {

		CoreUtil.validaParametrosObrigatorios(material);

		List<SceEstoqueAlmoxarifado> listaEstoqueAlmoxarifado = this.getSceEstoqueAlmoxarifadoDAO()
				.pesquisarEstoqueAlmoxarifadoMaterialSemSaldo(material.getCodigo());

		if (listaEstoqueAlmoxarifado != null && !listaEstoqueAlmoxarifado.isEmpty()) {

			for (SceEstoqueAlmoxarifado estoqueAlmoxarifado : listaEstoqueAlmoxarifado) {

				// Atualiza o estoque almoxarifado de acordo com a situação estocável do material
				estoqueAlmoxarifado.setIndSituacao(DominioSituacao.I);

				this.atualizar(estoqueAlmoxarifado, nomeMicrocomputador, true);

			}

		}

	}

	/**
	 * ORADB PROCEDURE SCEK_EAL_RN.RN_EALP_ATU_SIT_ATIV Ativa o material no
	 * estoque
	 * 
	 * @param material
	 */
	public void ativarEstoqueAlmoxarifadoPorMaterial(ScoMaterial material, String nomeMicrocomputador) throws BaseException {

		CoreUtil.validaParametrosObrigatorios(material);

		List<SceEstoqueAlmoxarifado> listaEstoqueAlmoxarifado = this.getSceEstoqueAlmoxarifadoDAO()
				.pesquisarEstoqueAlmoxarifadoPorMaterial(material.getCodigo());

		if (listaEstoqueAlmoxarifado != null && !listaEstoqueAlmoxarifado.isEmpty()) {

			for (SceEstoqueAlmoxarifado estoqueAlmoxarifado : listaEstoqueAlmoxarifado) {

				// Atualiza o estoque almoxarifado de acordo com a situação estocável do material
				estoqueAlmoxarifado.setIndSituacao(DominioSituacao.A);

				this.atualizar(estoqueAlmoxarifado, nomeMicrocomputador, true);

			}

		}

	}

	/**
	 * ORADB FUNCTION SCOC_VER_MATERIAL Verifica a existência e saldo em estoque
	 * almoxarifado
	 * 
	 * @param codigoMaterial
	 * @param seqAlmoxarifado
	 * @param numeroFornecedor
	 * @return
	 */
	public Boolean existeSaldoEstoqueAlmoxarifado(Integer codigoMaterial, Short seqAlmoxarifado, Integer numeroFornecedor) {
		CoreUtil.validaParametrosObrigatorios(codigoMaterial);
		return this.getSceEstoqueAlmoxarifadoDAO().existeSaldoEstoqueAlmoxarifado(codigoMaterial, seqAlmoxarifado, numeroFornecedor);
	}

	/**
	 * ORADB PROCEDURE SCEK_EAL_RN.RN_EALP_ATU_EAL_MAT Inclusão estoque
	 * almoxarifado através da inclusão do material
	 * 
	 * @param material
	 */
	public void gerarEstoqueAlmoxarifadoCentralMaterial(ScoMaterial material) throws BaseException {

		final AghParametros parametroAlmoxCentral = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_ALMOX_CENTRAL);
		final Short seqAlmoxarifado = parametroAlmoxCentral.getVlrNumerico().shortValue();
		SceAlmoxarifado almoxarifadoCentral = this.getSceAlmoxarifadoDAO().obterAlmoxarifadoPorSeq(seqAlmoxarifado);

		if (almoxarifadoCentral == null) {
			throw new ApplicationBusinessException(SceEstoqueAlmoxarifadoRNExceptionCode.SCE_00018);
		}

		this.gerarEstoqueAlmoxarifadoMaterial(material, almoxarifadoCentral);

	}

	/**
	 * ORADB PROCEDURE SCEK_EAL_RN.RN_EALP_ATU_EAL_LOC Inclusão estoque
	 * almoxarifado através da inclusão do material
	 * 
	 * @param material
	 */
	public void gerarEstoqueAlmoxarifadoMaterial(ScoMaterial material, SceAlmoxarifado almoxarifado) throws BaseException {
		SceEstoqueAlmoxarifado estoqueAlmoxarifado = new SceEstoqueAlmoxarifado();

		estoqueAlmoxarifado.setAlmoxarifado(almoxarifado);
		estoqueAlmoxarifado.setMaterial(material);
		estoqueAlmoxarifado.setUnidadeMedida(material.getUnidadeMedida());
		estoqueAlmoxarifado.setIndEstocavel(material.getIndEstocavelBoolean());

		estoqueAlmoxarifado.setIndEstqMinCalc(false);
		estoqueAlmoxarifado.setIndPontoPedidoCalc(false);
		estoqueAlmoxarifado.setIndSituacao(DominioSituacao.A);

		AghParametros parametroFornecedor = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
		ScoFornecedor fornecedor = this.getComprasFacade().obterFornecedorPorNumero(parametroFornecedor.getVlrNumerico().intValue());
		estoqueAlmoxarifado.setFornecedor(fornecedor);

		if (estoqueAlmoxarifado.getQtdeBloqueada() == null) {
			estoqueAlmoxarifado.setQtdeBloqueada(0);
		}

		if (estoqueAlmoxarifado.getQtdeDisponivel() == null) {
			estoqueAlmoxarifado.setQtdeDisponivel(0);
		}

		if (estoqueAlmoxarifado.getQtdeEmUso() == null) {
			estoqueAlmoxarifado.setQtdeEmUso(0);
		}

		if (estoqueAlmoxarifado.getQtdePontoPedido() == null) {
			estoqueAlmoxarifado.setQtdePontoPedido(0);
		}

		if (estoqueAlmoxarifado.getQtdeEstqMin() == null) {
			estoqueAlmoxarifado.setQtdeEstqMin(0);
		}

		if (estoqueAlmoxarifado.getQtdeEstqMax() == null) {
			estoqueAlmoxarifado.setQtdeEstqMax(0);
		}

		if (estoqueAlmoxarifado.getTempoReposicao() == null) {
			estoqueAlmoxarifado.setTempoReposicao(0);
		}

		estoqueAlmoxarifado.setIndControleValidade(false);

		estoqueAlmoxarifado.setIndConsignado(false);

		estoqueAlmoxarifado.setQtdeBloqEntrTransf(0);

		this.inserir(estoqueAlmoxarifado);
	}

	public void verificaEstoqueAlomxarifadoExistente(SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado) throws ApplicationBusinessException {

		if (getSceEstoqueAlmoxarifadoDAO().verificaEstoqueAlomxarifadoExistente(sceEstoqueAlmoxarifado)) {
			throw new ApplicationBusinessException(SceEstoqueAlmoxarifadoRNExceptionCode.ERRO_ESTOQUE_ALMOXARIFADO_CHAVE_UNICA);
		}
	}

	/**
	 * Busca os dados estatísticos do material/almoxarifado pesquisado
	 * 
	 * @param almoxSeq
	 * @param codMaterial
	 * @param dtCompetencia
	 * @return {@link EstatisticaEstoqueAlmoxarifadoVO}
	 * @throws BaseException
	 */
	public EstatisticaEstoqueAlmoxarifadoVO obterEstatisticasAlmoxarifadoPorMaterialAlmoxDataComp(Short almoxSeq, Short almSeqConsumo,
			Integer codMaterial, Date dtCompetencia) throws BaseException {
		AghParametros parametroFornecedor = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
		SceEstoqueAlmoxarifado ealm = getSceEstoqueAlmoxarifadoDAO().obterEstoqueAlmoxarifadoEstocavelPorMaterialAlmoxarifadoFornecedor(
				almoxSeq, codMaterial, parametroFornecedor.getVlrNumerico().intValue());

		if (ealm == null) {
			throw new ApplicationBusinessException(SceEstoqueAlmoxarifadoRNExceptionCode.SCE_00630_2);
		}

		EstatisticaEstoqueAlmoxarifadoVO estatistica = new EstatisticaEstoqueAlmoxarifadoVO();
		estatistica.setQtdePontoPerdido(ealm.getQtdePontoPedido());
		estatistica.setTempoReposicao(ealm.getTempoReposicao());
		estatistica.setCalculaPontoPedido(DominioSimNao.getInstance(ealm.getIndPontoPedidoCalc()).getDescricao());
		estatistica.setEndereco(ealm.getEndereco());
		estatistica.setSaldoBloqueado(ealm.getQtdeBloqueada());
		estatistica.setQtdeDisponivel(ealm.getQtdeDisponivel());
		estatistica.setQtdEntregaPendente(getAutFornecimentoFacade().obterQtdeEntregaPendente(codMaterial));

		popularDuracaoLocalDias(estatistica);
		/**
		 * Setar esse campo quando o modulo de compras estiver implantado.
		 * estatistica.setQtdePendenteConfirm(getSceItemRecebProvisorioDAO().
		 * obterQtdePendente(codMaterial));
		 */

		/* Popula histórico de consumo */
		popularHistoricoConsumo(estatistica, codMaterial, almSeqConsumo, dtCompetencia);

		/* Popula consumo médio */
		popularConsumoMedio(estatistica);

		/* popula consumo ponderado */
		popularConsumoPonderado(estatistica);

		/* Espaço disponível */
		if (ealm.getQtdeEstqMax() != null) {

			estatistica.setEspacoDisponivel(ealm.getQtdeEstqMax() - (ealm.getQtdeBloqueada() + ealm.getQtdeDisponivel()));

		} else {

			estatistica.setEspacoDisponivel(ealm.getQtdeBloqueada() + ealm.getQtdeDisponivel());

		}

		/* Bloqueado dispensacao */
		estatistica.setQtdeBloqueadoDispensacao(ealm.getQtdeBloqDispensacao());

		/* Popula Hospital */
		popularSaldosHospital(estatistica, ealm, dtCompetencia);

		/* Popula terceiros */
		popularTerceiros(estatistica, codMaterial, almoxSeq, dtCompetencia, parametroFornecedor.getVlrNumerico().intValue());

		/* Popula datas */
		popularDatasConsumo(estatistica, codMaterial);

		popularClassABC(estatistica, codMaterial, dtCompetencia, parametroFornecedor.getVlrNumerico().intValue());
		
		/*----*/
		return estatistica;
	}

	private void popularTerceiros(EstatisticaEstoqueAlmoxarifadoVO estatistica, Integer codMaterial, Short almoxSeq, Date dtCompetencia,
			Integer parametroFornecedor) {
		/* Terceiros */
		SceEstoqueAlmoxarifado ealmTerceiros = getSceEstoqueAlmoxarifadoDAO().pesquisarSaldosEstoqueTerceiros(codMaterial, almoxSeq,
				parametroFornecedor);
		estatistica.setSaldoBloqueadoTerceiro(ealmTerceiros.getQtdeBloqueada() == null ? 0 : ealmTerceiros.getQtdeBloqueada());
		estatistica.setQtdeDisponivelTerceiro(ealmTerceiros.getQtdeDisponivel() == null ? 0 : ealmTerceiros.getQtdeDisponivel());
		//saldo total

		Integer saldoTotalTerceiros = getSceEstoqueGeralDAO().obterSaldoTotalTerceiros(codMaterial, parametroFornecedor);
		if (saldoTotalTerceiros != null) {
			estatistica.setSaldoTodosAlmoxTerceiro(saldoTotalTerceiros);
		} else {
			estatistica.setSaldoTodosAlmoxTerceiro(0);
		}

	}

	private void popularDatasConsumo(EstatisticaEstoqueAlmoxarifadoVO estatistica, Integer codMaterial) {
		estatistica.setDataUltimaCompra(getSceEstoqueAlmoxarifadoDAO().obterDataUltimaCompra(codMaterial));		
		estatistica.setDataUltimoConsumo(getSceEstoqueAlmoxarifadoDAO().obterDataUltimoConsumo(codMaterial));
		estatistica.setDataUltimaCompraFundoFixo(getSceEstoqueAlmoxarifadoDAO().obterDataUltimaCompraFundoFixo(codMaterial));
	}

	private void popularDuracaoLocalDias(EstatisticaEstoqueAlmoxarifadoVO estatistica) {
		Integer totalMaterial = estatistica.getSaldoBloqueado() + estatistica.getQtdeDisponivel();

		if (totalMaterial != null && totalMaterial.intValue() > 0 && estatistica.getQtdePontoPerdido() != null
				&& estatistica.getTempoReposicao() != null && estatistica.getQtdePontoPerdido().intValue() > 0
				&& estatistica.getTempoReposicao().intValue() > 0) {
			Double valorDiv = (estatistica.getQtdePontoPerdido().doubleValue() / estatistica.getTempoReposicao().doubleValue());

			if (valorDiv.doubleValue() > 0.0) {
				estatistica.setDuracaoLocalDias(new DecimalFormat("#0.0#").format(totalMaterial.doubleValue() / valorDiv));

			} else {
				estatistica.setDuracaoLocalDias(_0_0);
			}
		} else {
			estatistica.setDuracaoLocalDias(_0_0);
		}
	}

	private void popularDuracaoGeralDias(EstatisticaEstoqueAlmoxarifadoVO estatistica, SceEstoqueGeral etqGeral) {

		if (etqGeral != null && etqGeral.getQtde().intValue() > 0 && estatistica.getQtdePontoPerdido() != null
				&& estatistica.getTempoReposicao() != null && estatistica.getQtdePontoPerdido().intValue() > 0
				&& estatistica.getTempoReposicao().intValue() > 0) {
			Double valorDiv = (estatistica.getQtdePontoPerdido().doubleValue() / estatistica.getTempoReposicao().doubleValue());

			if (valorDiv.doubleValue() > 0.0) {

				estatistica.setDuracaoGeralDias(new DecimalFormat("#0.0#").format(etqGeral.getQtde().doubleValue()
						/ (estatistica.getQtdePontoPerdido().doubleValue() / estatistica.getTempoReposicao().doubleValue())));
			} else {
				estatistica.setDuracaoGeralDias(_0_0);
			}
		} else {
			estatistica.setDuracaoGeralDias(_0_0);
		}
	}

	/**
	 * Uma parte da procedure referente a histórico de consumo, outras partes
	 * estão divididas ORADB P_POPULA_CONSUMOS
	 * 
	 * @param estatistica
	 * @throws ApplicationBusinessException
	 */
	public void popularHistoricoConsumo(EstatisticaEstoqueAlmoxarifadoVO estatistica, Integer codMaterial, Short almoxSeq,
			Date dtCompetencia) throws ApplicationBusinessException {
		AghParametros parametro = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COMPETENCIA);
		Date dtComptenciaSistema = parametro.getVlrData();

		estatistica.setHistoricoConsumo(getSceConsumoTotalMateriaisDAO().populaEstatisticasDeConsumoEmUmAnoPorMaterialAlmoxDtCompetencia(
				codMaterial, almoxSeq, dtCompetencia, dtComptenciaSistema));
	}

	/**
	 * Uma parte da procedure referente a consumo médio, outras partes estão
	 * divididas ORADB P_POPULA_CONSUMOS
	 * 
	 * @param estatistica
	 */
	public void popularConsumoMedio(EstatisticaEstoqueAlmoxarifadoVO estatistica) {
		Double valorMedio = (estatistica.getHistoricoConsumo().getQtdeMesAnterior1().doubleValue()
				+ estatistica.getHistoricoConsumo().getQtdeMesAnterior2().doubleValue()
				+ estatistica.getHistoricoConsumo().getQtdeMesAnterior3().doubleValue()
				+ estatistica.getHistoricoConsumo().getQtdeMesAnterior4().doubleValue()
				+ estatistica.getHistoricoConsumo().getQtdeMesAnterior5().doubleValue() + estatistica.getHistoricoConsumo()
				.getQtdeMesAnterior6().doubleValue()) / 6;
		estatistica.setQtdeConsumoMedio(new DecimalFormat("#0.0#").format(valorMedio));
	}

	private void popularClassABC(EstatisticaEstoqueAlmoxarifadoVO estatistica, Integer codMaterial, Date dtCompetencia,
			Integer parametroFornecedor) throws ApplicationBusinessException {
		SceEstoqueGeral etqGeral = getSceEstoqueGeralDAO().pesquisarEstoqueGeralPorMatDtCompFornecedor(codMaterial, dtCompetencia,
				parametroFornecedor);
		popularDuracaoGeralDias(estatistica, etqGeral);
		if (etqGeral != null) {
			if (etqGeral.getClassificacaoAbc() != null && etqGeral.getSubClassificacaoAbc() != null) {
				estatistica.setClassifABC(etqGeral.getClassificacaoAbc().getDescricao() + etqGeral.getSubClassificacaoAbc().getDescricao());
			} else {
				estatistica.setClassifABC("");
			}
		}
	}

	private void popularSaldosHospital(EstatisticaEstoqueAlmoxarifadoVO estatistica, SceEstoqueAlmoxarifado ealm, Date dtCompetencia)
			throws ApplicationBusinessException {
		/* Bloqueado problema */
		estatistica.setQtdeBloqueadoProblema(getSceHistoricoProblemaMaterialDAO().obterQtdeProblemaPorCodMaterial(
				ealm.getMaterial().getCodigo(), null, null));

		//saldo total hospital
		Integer saldoTotalHospital = getSceEstoqueGeralDAO().obterSaldoTotalHospital(ealm.getMaterial().getCodigo(),
				ealm.getFornecedor().getNumero());
		if (saldoTotalHospital != null) {
			estatistica.setSaldoTodosAlmox(saldoTotalHospital);
		} else {
			estatistica.setSaldoTodosAlmox(0);
		}

		// Recebimento Provisório
		if (ealm.getAlmoxarifado().equals(ealm.getMaterial().getAlmoxarifado())) {
			QtdeRpVO qtdeRp = new QtdeRpVO();

			Integer qtde = (getSceItemRecebProvisorioDAO().somarQtdeItensNotaRecebProvisorio(ealm, dtCompetencia) != null ) ? getSceItemRecebProvisorioDAO().somarQtdeItensNotaRecebProvisorio(ealm, dtCompetencia).intValue() : 0;
			estatistica.setQtdeRecebProvisorio(qtde);
			qtdeRp.setQuantidade(Long.valueOf(qtde.toString()));
			qtdeRp.setNotasRecebimento(getSceItemRecebProvisorioDAO().pesquisarNotasRecebimentoProvisorio(ealm, QtdeRpVO.MAX_RPS + 1));

			for (NotaRecebimentoProvisorio notaRP : qtdeRp.getNotasRecebimento()) {
				if (notaRP.getDocumentoFiscalEntrada() != null) {
					Hibernate.initialize(notaRP.getDocumentoFiscalEntrada().getFornecedor());
					notaRP.setFornecedor(notaRP.getDocumentoFiscalEntrada().getFornecedor());
				}
			}

			if (qtdeRp.getQuantidade() > 0 || (qtdeRp.getNotasRecebimento() != null && !qtdeRp.getNotasRecebimento().isEmpty())) {
				qtdeRp.setMostrarAlerta(Boolean.TRUE);
			} else {
				qtdeRp.setMostrarAlerta(Boolean.FALSE);
			}

			estatistica.setQtdeRp(qtdeRp);
		}
	}

	/**
	 * Uma parte da procedure referente a consumo ponderado, outras partes estão
	 * divididas ORADB P_POPULA_CONSUMOS
	 * 
	 * @param estatistica
	 */
	public void popularConsumoPonderado(EstatisticaEstoqueAlmoxarifadoVO estatistica) {
		Double qtdeConsumoPonderado = 0.0;
		ConsumoPonderadoVO v_cont = new ConsumoPonderadoVO(0);
		ConsumoPonderadoVO v_maior_consumo = new ConsumoPonderadoVO(0);
		ConsumoPonderadoVO v_peso1 = new ConsumoPonderadoVO(1);
		ConsumoPonderadoVO v_peso2 = new ConsumoPonderadoVO(1);
		ConsumoPonderadoVO v_peso3 = new ConsumoPonderadoVO(1);
		ConsumoPonderadoVO v_peso4 = new ConsumoPonderadoVO(1);
		ConsumoPonderadoVO v_peso5 = new ConsumoPonderadoVO(1);
		ConsumoPonderadoVO v_peso6 = new ConsumoPonderadoVO(1);

		ConsumoPonderadoVO v_consumo1 = new ConsumoPonderadoVO(estatistica.getHistoricoConsumo().getQtdeMesAnterior1());
		ConsumoPonderadoVO v_consumo2 = new ConsumoPonderadoVO(estatistica.getHistoricoConsumo().getQtdeMesAnterior2());
		ConsumoPonderadoVO v_consumo3 = new ConsumoPonderadoVO(estatistica.getHistoricoConsumo().getQtdeMesAnterior3());
		ConsumoPonderadoVO v_consumo4 = new ConsumoPonderadoVO(estatistica.getHistoricoConsumo().getQtdeMesAnterior4());
		ConsumoPonderadoVO v_consumo5 = new ConsumoPonderadoVO(estatistica.getHistoricoConsumo().getQtdeMesAnterior5());
		ConsumoPonderadoVO v_consumo6 = new ConsumoPonderadoVO(estatistica.getHistoricoConsumo().getQtdeMesAnterior6());

		ponderaValores(v_consumo1.getValue(), v_consumo2.getValue(), v_consumo3.getValue(), v_consumo4.getValue(), v_consumo5.getValue(),
				v_consumo6.getValue(), v_maior_consumo, v_peso1, false, v_cont);
		ponderaValores(v_consumo2.getValue(), v_consumo1.getValue(), v_consumo3.getValue(), v_consumo4.getValue(), v_consumo5.getValue(),
				v_consumo6.getValue(), v_maior_consumo, v_peso2, false, v_cont);
		ponderaValores(v_consumo3.getValue(), v_consumo1.getValue(), v_consumo2.getValue(), v_consumo4.getValue(), v_consumo5.getValue(),
				v_consumo6.getValue(), v_maior_consumo, v_peso3, false, v_cont);
		ponderaValores(v_consumo4.getValue(), v_consumo1.getValue(), v_consumo2.getValue(), v_consumo3.getValue(), v_consumo5.getValue(),
				v_consumo6.getValue(), v_maior_consumo, v_peso4, false, v_cont);
		ponderaValores(v_consumo5.getValue(), v_consumo1.getValue(), v_consumo2.getValue(), v_consumo3.getValue(), v_consumo4.getValue(),
				v_consumo6.getValue(), v_maior_consumo, v_peso5, false, v_cont);
		ponderaValores(v_consumo6.getValue(), v_consumo1.getValue(), v_consumo2.getValue(), v_consumo3.getValue(), v_consumo4.getValue(),
				v_consumo5.getValue(), v_maior_consumo, v_peso6, false, v_cont);

		ponderaValores(v_consumo1.getValue(), v_consumo2.getValue(), v_consumo3.getValue(), v_consumo4.getValue(), v_consumo5.getValue(),
				v_consumo6.getValue(), v_maior_consumo, v_peso1, true, v_cont);
		ponderaValores(v_consumo2.getValue(), v_consumo1.getValue(), v_consumo3.getValue(), v_consumo4.getValue(), v_consumo5.getValue(),
				v_consumo6.getValue(), v_maior_consumo, v_peso2, true, v_cont);
		ponderaValores(v_consumo3.getValue(), v_consumo1.getValue(), v_consumo2.getValue(), v_consumo4.getValue(), v_consumo5.getValue(),
				v_consumo6.getValue(), v_maior_consumo, v_peso3, true, v_cont);
		ponderaValores(v_consumo4.getValue(), v_consumo1.getValue(), v_consumo2.getValue(), v_consumo3.getValue(), v_consumo5.getValue(),
				v_consumo6.getValue(), v_maior_consumo, v_peso4, true, v_cont);
		ponderaValores(v_consumo5.getValue(), v_consumo1.getValue(), v_consumo2.getValue(), v_consumo3.getValue(), v_consumo4.getValue(),
				v_consumo6.getValue(), v_maior_consumo, v_peso5, true, v_cont);
		ponderaValores(v_consumo6.getValue(), v_consumo1.getValue(), v_consumo2.getValue(), v_consumo3.getValue(), v_consumo4.getValue(),
				v_consumo5.getValue(), v_maior_consumo, v_peso6, true, v_cont);

		v_consumo1.setValue(v_consumo1.getValue() * v_peso1.getValue());
		v_consumo2.setValue(v_consumo2.getValue() * v_peso2.getValue());
		v_consumo3.setValue(v_consumo3.getValue() * v_peso3.getValue());
		v_consumo4.setValue(v_consumo4.getValue() * v_peso4.getValue());
		v_consumo5.setValue(v_consumo5.getValue() * v_peso5.getValue());
		v_consumo6.setValue(v_consumo6.getValue() * v_peso6.getValue());

		qtdeConsumoPonderado = (v_consumo1.getValue().doubleValue() + v_consumo2.getValue().doubleValue()
				+ v_consumo3.getValue().doubleValue() + v_consumo4.getValue().doubleValue() + v_consumo5.getValue().doubleValue() + v_consumo6
				.getValue().doubleValue()) / 10;

		estatistica.setQtdeConsumoPonderado(new DecimalFormat("#0.00#").format(qtdeConsumoPonderado));
	}

	private void ponderaValores(Integer w_con1, Integer w_con2, Integer w_con3, Integer w_con4, Integer w_con5, Integer w_con6,
			ConsumoPonderadoVO p_maior_consumo, ConsumoPonderadoVO peso, boolean despezaMaiorConsumo, ConsumoPonderadoVO vCont) {
		Integer w_maior_consumo = p_maior_consumo.getValue();

		if (despezaMaiorConsumo) {
			if (w_con1.intValue() == p_maior_consumo.getValue().intValue()) {
				w_con1 = 0;
			}

			if (w_con2.intValue() == p_maior_consumo.getValue().intValue()) {
				w_con2 = 0;
			}

			if (w_con3.intValue() == p_maior_consumo.getValue().intValue()) {
				w_con3 = 0;
			}

			if (w_con4.intValue() == p_maior_consumo.getValue().intValue()) {
				w_con4 = 0;
			}

			if (w_con5.intValue() == p_maior_consumo.getValue().intValue()) {
				w_con5 = 0;
			}

			if (w_con6.intValue() == p_maior_consumo.getValue().intValue()) {
				w_con6 = 0;
			}
		}

		if (vCont.getValue() < 2) {
			if (w_con1.intValue() >= w_con2.intValue() && w_con1.intValue() >= w_con3.intValue() && w_con1.intValue() >= w_con4.intValue()
					&& w_con1.intValue() >= w_con5.intValue() && w_con1.intValue() >= w_con6.intValue()) {
				w_maior_consumo = w_con1;
				peso.setValue(3);
				vCont.setValue(vCont.getValue() + 1);
			}
		}

		if (!despezaMaiorConsumo) {
			p_maior_consumo.setValue(w_maior_consumo);
		}
	}

	/**
	 * Verifica se um estoque-almoxarifado esta inativo
	 * 
	 * @ORADB SCEK_SCE_RN.RN_SCEP_VER_EAL_ATIV, SCEK_IDF_RN.RN_IDFP_VER_EAL_ATIV
	 * @param eal
	 * @throws BaseException
	 */
	public void verificarEstoqueAlmoxarifadoAtivo(Integer ealSeq) throws BaseException {
		SceEstoqueAlmoxarifado eal = this.getSceEstoqueAlmoxarifadoDAO().obterPorChavePrimaria(ealSeq);

		if (eal == null || eal.getIndSituacao().equals(DominioSituacao.I)) {
			throw new ApplicationBusinessException(SceEstoqueAlmoxarifadoRNExceptionCode.SCE_00292);
		}
	}

	/**
	 * Verifica se a unidade de medida do estoque-almoxarifado eh a mesma do
	 * material
	 * 
	 * @ORADB: SCEK_SCE_RN.RN_SCEP_VER_UMD_ESTOQ
	 * @param ealSeq
	 * @param umdCodigoMaterial
	 * @throws ApplicationBusinessException
	 */
	public void verificarUnidadeMedidaEstoqueAlmoxarifado(Integer ealSeq, String umdCodigoMaterial) throws ApplicationBusinessException {

		SceEstoqueAlmoxarifado estoqueAlmox = this.getSceEstoqueAlmoxarifadoDAO().obterPorChavePrimaria(ealSeq);

		if (umdCodigoMaterial != null && estoqueAlmox != null) {
			if (!estoqueAlmox.getUnidadeMedida().getCodigo().equals(umdCodigoMaterial)) {
				throw new ApplicationBusinessException(SceEstoqueAlmoxarifadoRNExceptionCode.SCE_00280);
			}
		} else {
			throw new ApplicationBusinessException(SceEstoqueAlmoxarifadoRNExceptionCode.SCE_00280);
		}
	}

	protected SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO() {
		return sceEstoqueAlmoxarifadoDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected SceEstoqueGeralDAO getSceEstoqueGeralDAO() {
		return sceEstoqueGeralDAO;
	}

	protected SceEstoqueGeralRN getSceEstoqueGeralRN() {
		return sceEstoqueGeralRN;
	}

	protected IComprasFacade getComprasFacade() {
		return this.comprasFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected SceAlmoxarifadoDAO getSceAlmoxarifadoDAO() {
		return sceAlmoxarifadoDAO;
	}

	protected SceConsumoTotalMateriaisDAO getSceConsumoTotalMateriaisDAO() {
		return sceConsumoTotalMateriaisDAO;
	}

	protected SceHistoricoProblemaMaterialDAO getSceHistoricoProblemaMaterialDAO() {
		return sceHistoricoProblemaMaterialDAO;
	}

	protected SceItemRecebProvisorioDAO getSceItemRecebProvisorioDAO() {
		return sceItemRecebProvisorioDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	protected IAutFornecimentoFacade getAutFornecimentoFacade() {
		return this.autFornecimentoFacade;
	}

}
