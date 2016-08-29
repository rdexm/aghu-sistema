package br.gov.mec.aghu.prescricaomedica.modelobasico.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MpmModeloBasicoModoUsoProcedimento;
import br.gov.mec.aghu.model.MpmModeloBasicoModoUsoProcedimentoId;
import br.gov.mec.aghu.model.MpmModeloBasicoPrescricao;
import br.gov.mec.aghu.model.MpmModeloBasicoProcedimento;
import br.gov.mec.aghu.model.MpmModeloBasicoProcedimentoId;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.model.MpmTipoModoUsoProcedimento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoModoUsoProcedimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoPrescricaoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoProcedimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmProcedEspecialDiversoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoModoUsoProcedimentoDAO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterProcedimentosModeloBasicoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterProcedimentosModeloBasicoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MpmModeloBasicoModoUsoProcedimentoDAO mpmModeloBasicoModoUsoProcedimentoDAO;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private MpmModeloBasicoPrescricaoDAO mpmModeloBasicoPrescricaoDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private MpmModeloBasicoProcedimentoDAO mpmModeloBasicoProcedimentoDAO;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@Inject
	private MpmTipoModoUsoProcedimentoDAO mpmTipoModoUsoProcedimentoDAO;
	
	@Inject
	private MpmProcedEspecialDiversoDAO mpmProcedEspecialDiversoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5317382176526168043L;

	public enum ManterProcedimentosModeloBasicoONExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_PROCEDIMENTO_NAO_INFORMADO, MENSAGEM_MODELO_PRESCRICAO_NAO_INFORMADO, MENSAGEM_TIPO_MODO_USO_NAO_INFORMADO, MENSAGEM_MODELO_PROCEDIMENTO_NAO_INFORMADO, MENSAGEM_MODELO_BASICO_PRESCRICAO_INVALIDO, MENSAGEM_PROCED_ESPECIAL_DIVERSO_NAO_INFORMADO, MENSAGEM_PROCED_ESPECIAL_DIVERSO_INVALIDO, MENSAGEM_PROCED_ESPECIAL_DIVERSO_INATIVO, MENSAGEM_PROCED_ESPECIAL_NAO_PERMITE_PRESCRICAO, MENSAGEM_TIPO_MODO_USO_INVALIDO, MENSAGEM_TIPO_MODO_USO_INATIVO, MENSAGEM_MODO_USO_EXIGE_QUANTIDADE, MENSAGEM_PROCED_REALIZADO_NO_LEITO_NAO_INFORMADO, MENSAGEM_PROCED_REALIZADO_NO_LEITO_INVALIDO, MENSAGEM_PROCED_REALIZADO_NO_LEITO_INATIVO, MENSAGEM_PROCED_REALIZADO_NO_LEITO_NAO_PERMITIDO, MENSAGEM_ERRO_BUSCA_PARAMETRO, MENSAGEM_MATERIAL_NAO_PERTENCE_GRUPO, MENSAGEM_MATERIAL_NAO_ENCONTRADO, MENSAGEM_MATERIAL_INATIVO, MENSAGEM_SERVIDOR_NAO_PODE_ALTERAR, MENSAGEM_SERVIDOR_NAO_EXISTE, MENSAGEM_SERVIDOR_NAO_INFORMADO, MENSAGEM_PARAMETRO_NAO_RETORNOU_DADOS;
	}

	public MpmModeloBasicoProcedimento obterModeloBasicoProcedimento(
			Integer seqModelo, Integer seqItemModelo) {
		MpmModeloBasicoProcedimentoId id = new MpmModeloBasicoProcedimentoId(
				seqModelo, seqItemModelo.shortValue());
		return this.getMpmModeloBasicoProcedimentoDAO().obterPorChavePrimaria(
				id);
	}

	public void incluir(
			MpmModeloBasicoProcedimento modeloBasicoProcedimento,
			List<MpmModeloBasicoModoUsoProcedimento> listaModoUsoProdedimentoEspecial)
			throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (modeloBasicoProcedimento == null) {
			throw new ApplicationBusinessException(
					ManterProcedimentosModeloBasicoONExceptionCode.MENSAGEM_PROCEDIMENTO_NAO_INFORMADO);
		}

		modeloBasicoProcedimento.setServidor(this.validaAssociacao());

		modeloBasicoProcedimento
				.setModeloBasicoPrescricao(validaAssociacao(modeloBasicoProcedimento
						.getModeloBasicoPrescricao()));

		// Procedimento Especial Diverso
		if (modeloBasicoProcedimento.getProcedEspecialDiverso() != null) {
			this.validaProcedimentoEspecialDiverso(modeloBasicoProcedimento);
		} else {
			// Procedimento Realizado no Leito
			if (modeloBasicoProcedimento.getProcedimentoCirurgico() != null) {
				this
						.validaProcedimentoRealizadoNoLeito(modeloBasicoProcedimento);
			} else {
				// Órteses/Protéses
				if (modeloBasicoProcedimento.getMaterial() != null) {
					this.validaOrteseProtese(modeloBasicoProcedimento);
				}
			}
		}

		this.getMpmModeloBasicoProcedimentoDAO().persistir(
				modeloBasicoProcedimento);
		this.getMpmModeloBasicoProcedimentoDAO().flush();

		Short seqModProcedimento = modeloBasicoProcedimento.getId().getSeq();

		// Insere lista de itens para Procedimento Especial Diverso
		if (listaModoUsoProdedimentoEspecial != null
				&& !listaModoUsoProdedimentoEspecial.isEmpty()) {
			for (MpmModeloBasicoModoUsoProcedimento modoUsoProcedimentoEspecial : listaModoUsoProdedimentoEspecial) {

				modoUsoProcedimentoEspecial
						.setModeloBasicoProcedimento(modeloBasicoProcedimento);

				// Monta ID
				MpmModeloBasicoModoUsoProcedimentoId id = new MpmModeloBasicoModoUsoProcedimentoId();
				id.setModeloBasicoPrescricaoSeq(modoUsoProcedimentoEspecial
						.getId().getModeloBasicoPrescricaoSeq());
				id.setModeloBasicoProcedimentoSeq(seqModProcedimento);
				id.setTipoModoUsoProcedimentoSeq(modoUsoProcedimentoEspecial
						.getId().getTipoModoUsoProcedimentoSeq());
				id.setTipoModoUsoSeqp(modoUsoProcedimentoEspecial.getId()
						.getTipoModoUsoSeqp());
				modoUsoProcedimentoEspecial.setId(id);

				// Servidor
				modoUsoProcedimentoEspecial.setServidor(servidorLogado);

				// D05 - Valida Modo de Uso
				validaModoUsoProcedimentoEspecial(modoUsoProcedimentoEspecial);

				this.getMpmModeloBasicoModoUsoProcedimentoDAO().persistir(
						modoUsoProcedimentoEspecial);
				this.getMpmModeloBasicoModoUsoProcedimentoDAO().flush();
			}
		}
	}

	private void validaProcedimentoEspecialDiverso(
			MpmModeloBasicoProcedimento modeloBasicoProcedimento)
			throws ApplicationBusinessException {
		
		//modeloBasicoProcedimento = getMpmModeloBasicoProcedimentoDAO().obterPorChavePrimaria(modeloBasicoProcedimento.getId(), true, MpmModeloBasicoProcedimento.Fields.PROCED_ESPECIAL_DIVERSO);

		validaAssociacaoProcedEspecialDiverso(modeloBasicoProcedimento
				.getProcedEspecialDiverso());

		if (!DominioSituacao.A.equals(modeloBasicoProcedimento
				.getProcedEspecialDiverso().getIndSituacao())) {
			throw new ApplicationBusinessException(
					ManterProcedimentosModeloBasicoONExceptionCode.MENSAGEM_PROCED_ESPECIAL_DIVERSO_INATIVO);
		}

		if (!modeloBasicoProcedimento.getProcedEspecialDiverso()
				.getPermitePrescricao()) {
			throw new ApplicationBusinessException(
					ManterProcedimentosModeloBasicoONExceptionCode.MENSAGEM_PROCED_ESPECIAL_NAO_PERMITE_PRESCRICAO);
		}
	}

	private void validaAssociacaoProcedEspecialDiverso(
			MpmProcedEspecialDiversos procedEspecialDiversos)
			throws ApplicationBusinessException {

		MpmProcedEspecialDiversos result = null;

		if (procedEspecialDiversos == null) {
			throw new ApplicationBusinessException(
					ManterProcedimentosModeloBasicoONExceptionCode.MENSAGEM_PROCED_ESPECIAL_DIVERSO_NAO_INFORMADO);
		}

		if (procedEspecialDiversos.getSeq() != null) {
			result = this.getMpmProcedEspecialDiversoDAO()
					.obterPorChavePrimaria(procedEspecialDiversos.getSeq());
		}

		if (result == null) {
			throw new ApplicationBusinessException(
					ManterProcedimentosModeloBasicoONExceptionCode.MENSAGEM_PROCED_ESPECIAL_DIVERSO_INVALIDO);
		}
	}

	private void validaModoUsoProcedimentoEspecial(
			MpmModeloBasicoModoUsoProcedimento modoUsoProcedimentoEspecial)
			throws BaseException {

		// Valida se existe modo de uso
		validaAssociacao(modoUsoProcedimentoEspecial
				.getTipoModoUsoProcedimento());

		if (!DominioSituacao.A.equals(modoUsoProcedimentoEspecial
				.getTipoModoUsoProcedimento().getIndSituacao())) {
			throw new ApplicationBusinessException(
					ManterProcedimentosModeloBasicoONExceptionCode.MENSAGEM_TIPO_MODO_USO_INATIVO);
		}

		if (modoUsoProcedimentoEspecial.getTipoModoUsoProcedimento()
				.getIndExigeQuantidade()
				&& modoUsoProcedimentoEspecial.getQuantidade() == null) {
			throw new ApplicationBusinessException(
					ManterProcedimentosModeloBasicoONExceptionCode.MENSAGEM_MODO_USO_EXIGE_QUANTIDADE);
		}
	}

	private void validaProcedimentoRealizadoNoLeito(
			MpmModeloBasicoProcedimento modeloBasicoProcedimento)
			throws ApplicationBusinessException {

		validaAssociacaoProcedimentoRealizadoNoLeito(modeloBasicoProcedimento
				.getProcedimentoCirurgico());

		if (!DominioSituacao.A.equals(modeloBasicoProcedimento
				.getProcedimentoCirurgico().getIndSituacao())) {
			throw new ApplicationBusinessException(
					ManterProcedimentosModeloBasicoONExceptionCode.MENSAGEM_PROCED_REALIZADO_NO_LEITO_INATIVO);
		}

		if (!modeloBasicoProcedimento.getProcedimentoCirurgico()
				.getIndProcRealizadoLeito()) {
			throw new ApplicationBusinessException(
					ManterProcedimentosModeloBasicoONExceptionCode.MENSAGEM_PROCED_REALIZADO_NO_LEITO_NAO_PERMITIDO);
		}
	}

	private void validaOrteseProtese(
			MpmModeloBasicoProcedimento modeloBasicoProcedimento)
			throws ApplicationBusinessException {

		// Localizar material, pode não existir ou não ser do grupo
		// ortese/protese
		if (this.getComprasFacade().obterScoMaterialPorChavePrimaria(
				modeloBasicoProcedimento.getMaterial().getCodigo()) == null) {
			throw new ApplicationBusinessException(
					ManterProcedimentosModeloBasicoONExceptionCode.MENSAGEM_MATERIAL_NAO_ENCONTRADO);
		}

		// Material(encontrado) deve estar ativo
		if (DominioSituacao.I.toString().equals(
				modeloBasicoProcedimento.getMaterial().isIndSituacaoBoolean())) {
			throw new ApplicationBusinessException(
					ManterProcedimentosModeloBasicoONExceptionCode.MENSAGEM_MATERIAL_INATIVO);
		}

		// Comparar o grupo do material com o retorno de agh_parametros, códigos
		// devem ser compatíveis
		this.validaGrupoMaterial(modeloBasicoProcedimento);
	}

	private void validaGrupoMaterial(
			MpmModeloBasicoProcedimento mpmModeloBasicoProcedimento)
			throws ApplicationBusinessException {
		try {
			IParametroFacade parametroFacade = this.getParametroFacade();
			AghParametros param = null;
			param = parametroFacade
					.buscarAghParametro(AghuParametrosEnum.GRPO_MAT_ORT_PROT);

			if (param == null) {
				throw new ApplicationBusinessException(
						ManterProcedimentosModeloBasicoONExceptionCode.MENSAGEM_PARAMETRO_NAO_RETORNOU_DADOS);
			}

			if (param.getVlrNumerico().intValue() != mpmModeloBasicoProcedimento
					.getMaterial().getGrupoMaterial().getCodigo().intValue()) {
				throw new ApplicationBusinessException(
						ManterProcedimentosModeloBasicoONExceptionCode.MENSAGEM_MATERIAL_NAO_PERTENCE_GRUPO);
			}

		} catch (ApplicationBusinessException e) {
			logError("Exceção ApplicationBusinessException capturada, lançada para cima.");
			throw new ApplicationBusinessException(
					ManterProcedimentosModeloBasicoONExceptionCode.MENSAGEM_ERRO_BUSCA_PARAMETRO);
		}
	}

	// Valida associacao do tipo de modo de uso do procedimento
	private void validaAssociacao(
			MpmTipoModoUsoProcedimento mpmTipoModoUsoProcedimento)
			throws ApplicationBusinessException {

		MpmTipoModoUsoProcedimento result = null;

		if (mpmTipoModoUsoProcedimento == null) {
			throw new ApplicationBusinessException(
					ManterProcedimentosModeloBasicoONExceptionCode.MENSAGEM_TIPO_MODO_USO_NAO_INFORMADO);
		}

		if (mpmTipoModoUsoProcedimento.getId() != null) {
			result = this.getMpmTipoModoUsoProcedimentoDAO()
					.obterPorChavePrimaria(mpmTipoModoUsoProcedimento.getId());
		}

		if (result == null) {
			throw new ApplicationBusinessException(
					ManterProcedimentosModeloBasicoONExceptionCode.MENSAGEM_TIPO_MODO_USO_INVALIDO);
		}

	}

	// Valida associacao do modelo basico de prescricao(modelo basico)
	private MpmModeloBasicoPrescricao validaAssociacao(
			MpmModeloBasicoPrescricao modeloBasicoPrescricao)
			throws ApplicationBusinessException {

		MpmModeloBasicoPrescricao result = null;

		if (modeloBasicoPrescricao == null) {
			throw new ApplicationBusinessException(
					ManterProcedimentosModeloBasicoONExceptionCode.MENSAGEM_MODELO_PRESCRICAO_NAO_INFORMADO);
		}

		if (modeloBasicoPrescricao.getSeq() != null) {
			result = this.getMpmModeloBasicoPrescricaoDAO()
					.obterPorChavePrimaria(modeloBasicoPrescricao.getSeq());
		}

		if (result == null) {
			throw new ApplicationBusinessException(
					ManterProcedimentosModeloBasicoONExceptionCode.MENSAGEM_MODELO_BASICO_PRESCRICAO_INVALIDO);
		}

		return result;
	}

	// Valida modelo basico de procedimento
	private MpmModeloBasicoProcedimento validaAssociacao(
			MpmModeloBasicoProcedimento mpmModeloBasicoProcedimento)
			throws ApplicationBusinessException {

		MpmModeloBasicoProcedimento result = null;

		if (mpmModeloBasicoProcedimento == null) {
			throw new ApplicationBusinessException(
					ManterProcedimentosModeloBasicoONExceptionCode.MENSAGEM_MODELO_PROCEDIMENTO_NAO_INFORMADO);
		}

		if (mpmModeloBasicoProcedimento.getId() != null) {
			result = this.getMpmModeloBasicoProcedimentoDAO()
					.obterPorChavePrimaria(mpmModeloBasicoProcedimento.getId());
		}
		return result;
	}

	private void validaAssociacaoProcedimentoRealizadoNoLeito(
			MbcProcedimentoCirurgicos procedimentoCirurgicos)
			throws ApplicationBusinessException {

		MbcProcedimentoCirurgicos result = null;

		if (procedimentoCirurgicos == null) {
			throw new ApplicationBusinessException(
					ManterProcedimentosModeloBasicoONExceptionCode.MENSAGEM_PROCED_REALIZADO_NO_LEITO_NAO_INFORMADO);
		}

		if (procedimentoCirurgicos.getSeq() != null) {
			result = this.getBlocoCirurgicoFacade()
					.obterMbcProcedimentoCirurgicosPorId(procedimentoCirurgicos.getSeq());
		}

		if (result == null) {
			throw new ApplicationBusinessException(
					ManterProcedimentosModeloBasicoONExceptionCode.MENSAGEM_PROCED_REALIZADO_NO_LEITO_INVALIDO);
		}
	}

	/**
	 * Valida a existência do servidor no sistema, utilizado para inserir
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private RapServidores validaAssociacao()
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// se não informado ou chave incompleta
		if (servidorLogado == null || servidorLogado.getId() == null
				|| servidorLogado.getId().getMatricula() == null
				|| servidorLogado.getId().getVinCodigo() == null) {
			return null;
		}
		return servidorLogado;
	}

	/**
	 * Compara o servidor informado(servidor do modelo) com o servidor logado
	 * para verificar o dono do modelo
	 * 
	 * @param servidorModelo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private Boolean validaServidor(RapServidores servidorModelo)
			throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (servidorModelo == null) {
			throw new ApplicationBusinessException(
					ManterProcedimentosModeloBasicoONExceptionCode.MENSAGEM_SERVIDOR_NAO_INFORMADO);
		}

		if (servidorModelo.equals(servidorLogado)) {
			return true;
		}
		return false;
	}

	public void alterar(
			MpmModeloBasicoProcedimento mpmModeloBasicoProcedimento,
			List<MpmModeloBasicoModoUsoProcedimento> mpmModeloBasicoModoUsoProcedimentos)
			throws BaseException {

		if (mpmModeloBasicoProcedimento == null) {
			throw new ApplicationBusinessException(
					ManterProcedimentosModeloBasicoONExceptionCode.MENSAGEM_PROCEDIMENTO_NAO_INFORMADO);
		}

		if (!this.validaServidor(mpmModeloBasicoProcedimento.getServidor())) {
			throw new ApplicationBusinessException(
					ManterProcedimentosModeloBasicoONExceptionCode.MENSAGEM_SERVIDOR_NAO_PODE_ALTERAR);
		}

		mpmModeloBasicoProcedimento
				.setModeloBasicoPrescricao(validaAssociacao(mpmModeloBasicoProcedimento
						.getModeloBasicoPrescricao()));

		// Procedimento Especial Diverso
		if (mpmModeloBasicoProcedimento.getProcedEspecialDiverso() != null) {
			this.validaProcedimentoEspecialDiverso(mpmModeloBasicoProcedimento);
		} else {
			// Procedimento Realizado no Leito
			if (mpmModeloBasicoProcedimento.getProcedimentoCirurgico() != null) {
				this
						.validaProcedimentoRealizadoNoLeito(mpmModeloBasicoProcedimento);
			} else {
				// Órteses/Protéses
				if (mpmModeloBasicoProcedimento.getMaterial() != null) {
					this.validaOrteseProtese(mpmModeloBasicoProcedimento);
				}
			}
		}

		MpmModeloBasicoPrescricao mpmModeloBasicoPrescricao = validaAssociacao(mpmModeloBasicoProcedimento
				.getModeloBasicoPrescricao());

		mpmModeloBasicoProcedimento
				.setModeloBasicoPrescricao(mpmModeloBasicoPrescricao);

		this.getMpmModeloBasicoProcedimentoDAO().merge(
				mpmModeloBasicoProcedimento);
		this.getMpmModeloBasicoProcedimentoDAO().flush();

		// A PARTIR DAQUI, AS EXCEPTIONS DEVEM REALIZAR ROLLBACK POIS JÁ FOI
		// FEITO FLUSH
		if (mpmModeloBasicoModoUsoProcedimentos != null
				&& !mpmModeloBasicoModoUsoProcedimentos.isEmpty()) {

			for (MpmModeloBasicoModoUsoProcedimento modoUsoProcedimentoEspecial : mpmModeloBasicoModoUsoProcedimentos) {
				
				if (this.validaServidor(modoUsoProcedimentoEspecial
						.getServidor())) {
					modoUsoProcedimentoEspecial
							.setModeloBasicoProcedimento(mpmModeloBasicoProcedimento);

					// Monta ID
					MpmModeloBasicoModoUsoProcedimentoId id = new MpmModeloBasicoModoUsoProcedimentoId();
					id.setModeloBasicoPrescricaoSeq(modoUsoProcedimentoEspecial
							.getId().getModeloBasicoPrescricaoSeq());
					id
							.setModeloBasicoProcedimentoSeq(mpmModeloBasicoProcedimento
									.getId().getSeq());
					id
							.setTipoModoUsoProcedimentoSeq(modoUsoProcedimentoEspecial
									.getId().getTipoModoUsoProcedimentoSeq());
					id.setTipoModoUsoSeqp(modoUsoProcedimentoEspecial.getId()
							.getTipoModoUsoSeqp());
					modoUsoProcedimentoEspecial.setId(id);

					// Servidor
					modoUsoProcedimentoEspecial
							.setServidor(mpmModeloBasicoProcedimento
									.getServidor());

					// Tipo Modo de Uso
					validaModoUsoProcedimentoEspecial(modoUsoProcedimentoEspecial);

					this.getMpmModeloBasicoModoUsoProcedimentoDAO().merge(
							modoUsoProcedimentoEspecial);
					this.getMpmModeloBasicoModoUsoProcedimentoDAO().flush();

				} else {
					throw new ApplicationBusinessException(
							ManterProcedimentosModeloBasicoONExceptionCode.MENSAGEM_SERVIDOR_NAO_PODE_ALTERAR);
				}
			}
		}
	}

	public List<MpmModeloBasicoProcedimento> obterListaProcedimentos(
			MpmModeloBasicoPrescricao mpmModeloBasicoPrescricao) {
		return this.getMpmModeloBasicoProcedimentoDAO().pesquisar(
				mpmModeloBasicoPrescricao);
	}

	public List<MpmModeloBasicoModoUsoProcedimento> obterListaModoDeUsoDoModelo(
			MpmModeloBasicoProcedimento mpmModeloBasicoProcedimento)
			throws ApplicationBusinessException {
		this.validaAssociacao(mpmModeloBasicoProcedimento);
		return this.getMpmModeloBasicoModoUsoProcedimentoDAO().pesquisar(
				mpmModeloBasicoProcedimento);
	}


    public void removeListaModoDeUsoDoModelo(MpmModeloBasicoProcedimento mpmModeloBasicoProcedimento) throws ApplicationBusinessException {
        List<MpmModeloBasicoModoUsoProcedimento> modoUsoList = obterListaModoDeUsoDoModelo(mpmModeloBasicoProcedimento);
        for(MpmModeloBasicoModoUsoProcedimento modoUso : modoUsoList){
            this.getMpmModeloBasicoModoUsoProcedimentoDAO().remover(this.getMpmModeloBasicoModoUsoProcedimentoDAO().obterPorChavePrimaria(modoUso.getId()));
        }
    }



	protected MpmModeloBasicoPrescricaoDAO getMpmModeloBasicoPrescricaoDAO() {
		return mpmModeloBasicoPrescricaoDAO;
	}

	private MpmModeloBasicoProcedimentoDAO getMpmModeloBasicoProcedimentoDAO() {
		return mpmModeloBasicoProcedimentoDAO;
	}

	private MpmModeloBasicoModoUsoProcedimentoDAO getMpmModeloBasicoModoUsoProcedimentoDAO() {
		return mpmModeloBasicoModoUsoProcedimentoDAO;
	}

	private MpmTipoModoUsoProcedimentoDAO getMpmTipoModoUsoProcedimentoDAO() {
		return mpmTipoModoUsoProcedimentoDAO;
	}

	private MpmProcedEspecialDiversoDAO getMpmProcedEspecialDiversoDAO() {
		return mpmProcedEspecialDiversoDAO;
	}

	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}