package br.gov.mec.aghu.estoque.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndOperacaoBasica;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.dao.SceAlmoxarifadoTransferenciaAutomaticaDAO;
import br.gov.mec.aghu.estoque.dao.SceDocumentoValidadeDAO;
import br.gov.mec.aghu.estoque.dao.SceItemTransferenciaDAO;
import br.gov.mec.aghu.estoque.dao.SceMovimentoMaterialDAO;
import br.gov.mec.aghu.estoque.dao.SceTipoMovimentosDAO;
import br.gov.mec.aghu.estoque.dao.SceTransferenciaDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifadoTransferenciaAutomatica;
import br.gov.mec.aghu.model.SceDocumentoValidade;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemTransferencia;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.SceTransferencia;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength"})
@Stateless
public class SceTransferenciaRN extends BaseBusiness{

	@EJB
	private SceDocumentoValidadeRN sceDocumentoValidadeRN;
	@EJB
	private SceValidadesRN sceValidadesRN;
	@EJB
	private SceMovimentoMaterialRN sceMovimentoMaterialRN;
	@EJB
	private ControlarValidadeMaterialRN controlarValidadeMaterialRN;
	@EJB
	private SceItemTransferenciaRN sceItemTransferenciaRN;
	@EJB
	private SceAlmoxarifadosRN sceAlmoxarifadosRN;
	@EJB
	private SceTipoMovimentosRN sceTipoMovimentosRN;
	@EJB
	private SceLoteDocumentoRN sceLoteDocumentoRN;
	
	private static final Log LOG = LogFactory.getLog(SceTransferenciaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private SceItemTransferenciaDAO sceItemTransferenciaDAO;
	
	@Inject
	private SceTransferenciaDAO sceTransferenciaDAO;
	
	@Inject
	private SceAlmoxarifadoTransferenciaAutomaticaDAO sceAlmoxarifadoTransferenciaAutomaticaDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private SceDocumentoValidadeDAO sceDocumentoValidadeDAO;
	
	@Inject
	private SceMovimentoMaterialDAO sceMovimentoMaterialDAO;
	
	@Inject
	private SceTipoMovimentosDAO sceTipoMovimentosDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6280584704224323281L;

	public enum SceTransferenciaRNExceptionCode implements BusinessExceptionCode {
		SCE_00476,SCE_00766,SCE_00670,SCE_00480,SCE_00481,SCE_00482,SCE_00408,SCE_00757,SCE_00299,SCE_00367,SCE_00801,SCE_00459,SCE_00282,SCE_00460,SCE_00293;
	}

	/*
	 * Métodos para Inserir SceTransferencia
	 */

	/**
	 * ORADB TRIGGER SCET_TRF_BRI (INSERT)
	 * @param transferencia
	 * @throws BaseException
	 */
	private void preInserir(SceTransferencia transferencia) throws BaseException{
		this.atualizarServidorGeradorTransferencia(transferencia); // RN1
		this.atualizarTipoMovimento(transferencia); // RN2
		this.validarAlmoxarifadoAtivo(transferencia); // RN3
		this.validarInclusao(transferencia);
		this.validarTransferenciasAutomaticas(transferencia); // RN4
	}
	
		/**
	 * Inserir SceTransferencia
	 * @param transferencia
	 * @throws BaseException
	 */
	public void inserir(SceTransferencia transferencia) throws BaseException{
		this.preInserir(transferencia);
		this.getSceTransferenciaDAO().persistir(transferencia);
	}
	
	/**
	 * Inserir SceTransferencia
	 * @param transferencia
	 * @throws BaseException
	 */
	public void inserirTrasnferenciaEventual(SceTransferencia transferencia) throws BaseException{
		
		transferencia.setTransferenciaAutomatica(Boolean.FALSE);
		transferencia.setEstorno(Boolean.FALSE);
		transferencia.setEfetivada(Boolean.FALSE);
		
		inserir(transferencia);
		
		
	}
	

	/**
	 * ORADB TRIGGER SCET_TRF_BRD (DELETE)
	 * @param transferencia
	 * @throws BaseException
	 */
	private void preRemover(SceTransferencia transferencia) throws BaseException{	
		this.validarRemoverTransferenciaEfetivada(transferencia); // RN1
	}

	/**
	 * Remover SceTransferencia
	 * @param transferencia
	 * @throws BaseException
	 */
	public void remover(SceTransferencia transferencia) throws BaseException{
		this.preRemover(transferencia);
		this.getSceTransferenciaDAO().remover(transferencia);
		this.getSceTransferenciaDAO().flush();
		this.removerItensTransferencia(transferencia); // RN adicional e descrita como melhoria no documento de análise
	}

	/**
	 * Remove todos os itens de transferência
	 * @param transferencia
	 * @throws BaseException
	 */
	private void removerItensTransferencia(SceTransferencia transferencia) throws BaseException{

		// Resgata a lista de itens de transferência
		List<SceItemTransferencia> listaItemTransferencia = this.getSceItemTransferenciaDAO().pesquisarListaItensTransferenciaPorTransferencia(transferencia.getSeq());	

		// Remove todos os itens de transferência
		for (SceItemTransferencia itemTransferencia : listaItemTransferencia) {
			this.getSceItemTransferenciaRN().remover(itemTransferencia);
		}
	}

	/*
	 * RNs Inserir
	 */

	/**
	 * ORADB PROCEDURE SCEK_TRF_RN.RN_TRFP_ATU_GERACAO
	 * Atualiza o servidor gerador da transferência
	 * @param transferencia
	 * @throws ApplicationBusinessException  
	 */
	public void atualizarServidorGeradorTransferencia(SceTransferencia transferencia) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		transferencia.setServidor(servidorLogado);
		transferencia.setDtGeracao(new Date());
	}

	/**
	 * Atualiza o tipo de movimento da transferência
	 * @param transferencia
	 */
	protected void atualizarTipoMovimento(SceTransferencia transferencia) throws BaseException{
		AghParametros parametroTipoMovimento = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_TR);
		SceTipoMovimento tipoMovimento = getSceTipoMovimentoDAO().obterSceTipoMovimentosAtivoPorSeq(parametroTipoMovimento.getVlrNumerico().shortValue());
		transferencia.setTipoMovimento(tipoMovimento);
	}

	/**
	 * ORADB PROCEDURE SCEK_TRF_RN.RN_TRFP_VER_ALM_ATIV
	 * Verifica se o almoxarifado está ativo
	 * @param transferencia
	 */
	public void validarAlmoxarifadoAtivo(SceTransferencia transferencia) throws ApplicationBusinessException{
		// Reutiliza SCEK_RMS_RN.RN_RMSP_VER_ALM_ATI
		this.getSceAlmoxarifadosRN().verificarAlmoxariadoAtivoPorId(transferencia.getAlmoxarifado().getSeq());
		this.getSceAlmoxarifadosRN().verificarAlmoxariadoAtivoPorId(transferencia.getAlmoxarifadoRecebimento().getSeq());
	}

	/**
	 * ORADB PROCEDURE SCEK_TRF_RN.RN_TRFP_VER_INCLUSAO
	 * Verifica a inclusão
	 * @param transferencia
	 */
	public void validarInclusao(SceTransferencia transferencia) throws BaseException{

		// Não é possível gerar transferência efetivadas
		if(Boolean.TRUE.equals(transferencia.getEfetivada())){
			throw new ApplicationBusinessException(SceTransferenciaRNExceptionCode.SCE_00480);
		}

		// Campos de efetivação não devem ser informados na inclusão
		if(transferencia.getDtEfetivacao() != null || transferencia.getServidorEfetivado() !=null){
			throw new ApplicationBusinessException(SceTransferenciaRNExceptionCode.SCE_00481);
		}

	}

	/**
	 * ORADB PROCEDURE SCEK_TRF_RN.RN_TRFP_VER_REL_ATA
	 * Verifica as transferências automáticas
	 * @param transferencia
	 */
	public void validarTransferenciasAutomaticas(SceTransferencia transferencia) throws ApplicationBusinessException{

		// Verifica se a transferência é automática
		if(transferencia.getTransferenciaAutomatica()){

			final Short almSeq =  transferencia.getAlmoxarifado().getSeq();
			final Short almSeqRecebe = transferencia.getAlmoxarifadoRecebimento().getSeq();

			SceAlmoxarifadoTransferenciaAutomatica transferenciaAutomatica = this.getSceAlmoxarifadoTransferenciaAutomaticaDAO().obterAlmoxarifadoTransferenciaAutomaticaPorAlmoxarifadoOrigemDestino(almSeq, almSeqRecebe);

			// Verifica se a transferência entre almoxarifados foi prevista	e está ativa	
			if(transferenciaAutomatica == null){
				throw new ApplicationBusinessException(SceTransferenciaRNExceptionCode.SCE_00670);
			} else if (DominioSituacao.I.equals(transferenciaAutomatica.getSituacao())){
				throw new ApplicationBusinessException(SceTransferenciaRNExceptionCode.SCE_00766);
			}

		}

	}


	/*
	 * RNs Remover
	 */

	/**
	 * ORADB PROCEDURE SCEK_TRF_RN.RN_TRFP_VER_EXCLUSAO
	 * Não é possível excluir transferências que já estejam efetivadas
	 * @param transferencia
	 * @throws BaseException
	 */
	public void validarRemoverTransferenciaEfetivada(SceTransferencia transferencia) throws ApplicationBusinessException{
		if(Boolean.TRUE.equals(transferencia.getEfetivada())){
			throw new ApplicationBusinessException(SceTransferenciaRNExceptionCode.SCE_00476);
		}
	}

	public void atualizar(SceTransferencia transferencia, String nomeMicrocomputador) throws BaseException{
		SceTransferencia oldTransferencia = this.getSceTransferenciaDAO().obterOriginal(transferencia.getSeq());
		atualizar(transferencia, oldTransferencia, nomeMicrocomputador);
	}
	
	public void atualizar(SceTransferencia transferencia, SceTransferencia  oldTransferencia, String nomeMicrocomputador) throws BaseException{
		this.preAtualizar(transferencia, oldTransferencia, nomeMicrocomputador);
		this.getSceTransferenciaDAO().merge(transferencia);
		this.getSceTransferenciaDAO().flush();


		this.posAtualizar(transferencia,oldTransferencia, nomeMicrocomputador);

	}

	/**
	 * ORADB PROCEDURE SCEP_ENFORCE_TRF_RULES
	 * @param transferencia
	 * @throws BaseException
	 */
	private void posAtualizar(SceTransferencia transferencia,SceTransferencia oldTransferencia, String nomeMicrocomputador) throws BaseException{

		if (!oldTransferencia.getEfetivada().equals(transferencia.getEfetivada()) && transferencia.getEfetivada() && !transferencia.getEstorno()) {

			/**
			 * Transfere Material consignado para o HCPA
			 */
			//			if(!transferencia.getAlmoxarifado().equals(transferencia.getAlmoxarifadoRecebimento())){
			//				if(transferencia.getAlmoxarifado().getSeq() == 1){ //criar parâmetro!!!!!!!!!!!!!
			//
			//					/**
			//					 *  SCEK_TRF_RN.RN_TRFP_MAT_CONSIGN(l_trf_row_new.seq);
			//					 */
			//
			//				}
			//
			//			}
			/* GERA INCLUSÃO NA TABELA MOVIMENTO_MATERIAIS PARA TODOS  OS  ITENS DA TR  */
			this.atualizaMovimentoTransferencia(transferencia, nomeMicrocomputador);

		}

	}


	/**
	 * ORADB PROCEDURE SCEK_TRF_RN.RN_TRFP_ATU_MVTO
	 * @param transferencia
	 * @throws ApplicationBusinessException
	 * @throws BaseException
	 */
	private void atualizaMovimentoTransferencia(SceTransferencia transferencia, String nomeMicrocomputador) throws BaseException{

		List<SceItemTransferencia> listItemTransferencia = this.getSceItemTransferenciaDAO().pesquisarListaItensTransferenciaPorTransferencia(transferencia.getSeq());

		for(SceItemTransferencia itemTransf: listItemTransferencia){

			/*  quando efetivar e quantidade enviada for NULL, então a quantidade enviada deve ser considerada = a quantidade */
			if(itemTransf.getQtdeEnviada() == null && itemTransf.getQuantidade() != null){

				itemTransf.setQtdeEnviada(itemTransf.getQuantidade());

			}

			/* Faz o insert do registro na tabela de movimento de materiais */
			if(itemTransf.getQtdeEnviada()!=null && itemTransf.getQtdeEnviada() > 0){

				this.getSceMovimentoMaterialRN().atualizarMovimentoMaterial(transferencia.getAlmoxarifado(),
						itemTransf.getEstoqueAlmoxarifado().getMaterial(),
						itemTransf.getEstoqueAlmoxarifado().getUnidadeMedida(),
						itemTransf.getQtdeEnviada(),
						itemTransf.getQuantidade(), Boolean.FALSE,
						transferencia.getTipoMovimento(),
						null,
						transferencia.getSeq(),
						null,
						null,
						itemTransf.getEstoqueAlmoxarifado().getFornecedor(), //Forn de saída do material
						null,
						null,
						transferencia.getAlmoxarifadoRecebimento(),
						null,
						null, nomeMicrocomputador, true);

				SceEstoqueAlmoxarifado ealOrigem = itemTransf.getEstoqueAlmoxarifadoOrigem();
				SceEstoqueAlmoxarifado ealSeqDestino = itemTransf.getEstoqueAlmoxarifado();
				//	Short tmvSeq = transferencia.getTipoMovimento().getId().getSeq();
				//	Byte tmvComplemento = transferencia.getTipoMovimento().getId().getComplemento();
				SceTipoMovimento tipoMovimento = transferencia.getTipoMovimento();
				Integer nroDocumento = transferencia.getSeq();
				Integer quantidade = itemTransf.getQuantidade();

				// Chamada da procedure SCEK_VAL_ATUALIZACAO.SCEP_TRF_ATU_VALID
				this.getControlarValidadeMaterialRN().atualizarValidadeTransferenciaAutomatica(ealOrigem, ealSeqDestino, tipoMovimento,nroDocumento,quantidade);

			}

		}



	}





	/**
	 * ORADB TRIGGER "AGH".SCET_TRF_BRU (UPDATE)
	 * @param transferencia
	 * @throws BaseException
	 */
	private void preAtualizar(SceTransferencia transferencia,SceTransferencia oldTransferencia, String nomeMicrocomputador) throws BaseException{

		this.validarAtualizacao(transferencia,oldTransferencia); // RN1,RN2,RN3 (scek_trf_rn.rn_trfp_ver_alterac), 

		this.atualizarEfetivacao(transferencia,oldTransferencia);//RN4 - Atualiza TipoMovimento e Servidor
		this.validarTipoMovimentoEfetivacao(transferencia,oldTransferencia);
		this.validarAlmoxarifadoAtivo(transferencia); // RN5
		this.atualizaEstornoTransferencia(transferencia,oldTransferencia, nomeMicrocomputador);//RN6
	}

	/**
	 * RN6
	 * @param transferencia
	 * @param oldTransferencia
	 * @throws BaseException
	 */
	private void atualizaEstornoTransferencia(SceTransferencia transferencia,SceTransferencia oldTransferencia, String nomeMicrocomputador) throws BaseException{


		if(oldTransferencia.getEfetivada().equals(transferencia.getEfetivada()) && transferencia.getEfetivada()){

			if(!oldTransferencia.getEstorno().equals(transferencia.getEstorno()) && transferencia.getEstorno()){

				if(transferencia.getTipoMovimento()!=null){

					this.atualizaEstornoEfetivacaoTransf(transferencia);

					this.atualizaMvtoEstorno(transferencia, nomeMicrocomputador);


				}

			}

		}


	}

	/**
	 * ORADB SCEK_TRF_RN.RN_TRFP_ATU_MVTO_EST
	 * @param transferencia
	 * @throws ApplicationBusinessException
	 * @throws BaseException
	 */
	private void atualizaMvtoEstorno(SceTransferencia transferencia, String nomeMicrocomputador)
	throws ApplicationBusinessException, BaseException {
		Integer novaQtde = null;
		List<SceItemTransferencia> listItemTransferencia = this.getSceItemTransferenciaDAO().pesquisarListaItensTransferenciaPorTransferencia(transferencia.getSeq());

		Boolean encontrouMov = false;
		for(SceItemTransferencia itemTransf: listItemTransferencia){

			/*
			 * Verifica se houve alteração na unidade de medida do movimento 
			 * e a unidade de medida atual (do material).
			 */
			encontrouMov = true;


			novaQtde = itemTransf.getQtdeEnviada();

			

			/*
				  Faz o insert do registro na tabela de movimento
			--	  de materiais, sendo marcado como estornado.
			--------------------------------------------------------------------------------
			--    Chama rotina que insere na movimento materiais o
			--    movimento de estorno do tipo correspondente.
			--------------------------------------------------------------------------------			
			 */
			this.getSceMovimentoMaterialRN().atualizarMovimentoMaterial(transferencia.getAlmoxarifado(),
					itemTransf.getEstoqueAlmoxarifado().getMaterial(),
					itemTransf.getEstoqueAlmoxarifado().getUnidadeMedida(),
					novaQtde,
					itemTransf.getQuantidade(),
					Boolean.TRUE,
					transferencia.getTipoMovimento(),
					null,
					transferencia.getSeq(),
					null,
					null,
					itemTransf.getEstoqueAlmoxarifado().getFornecedor(),
					null,
					null,
					transferencia.getAlmoxarifadoRecebimento(),
					null,
					null, nomeMicrocomputador, true);



			//this.atualizarValidade(transferencia, itemTransf);

		}

		if (!encontrouMov) {

			throw new ApplicationBusinessException(SceTransferenciaRNExceptionCode.SCE_00293);

		}



	}

	/**
	 * ORADB PROCEDURE SCEP_TRF_EST_VALID
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity", "ucd"})
	protected void atualizarValidade(SceTransferencia transferencia, SceItemTransferencia itemTransf) throws ApplicationBusinessException, BaseException {

		Boolean achouProx = false;
		Boolean achouDocValidade = false;
		Boolean achouDocValidProx = false;
		Integer qtdeDoc = 0;
		Integer qtdeEntrada = 0;
		Integer qtdeConsumo = 0;
		Integer qtdeDisponivel = 0;

		Integer qtdeEntradaProx = 0;
		Integer qtdeConsumoProx = 0;
		Integer qtdeDisponivelProx = 0;


		if(transferencia.getTipoMovimento()==null){
			/* Tipo de Movimento não Cadastrado */
			throw new ApplicationBusinessException(SceTransferenciaRNExceptionCode.SCE_00459);

		}else{

			if(transferencia.getTipoMovimento().getTipoMovimentoProximo()==null){
				/* Tipo de Movimento não Cadastrado ou inativo */
				throw new ApplicationBusinessException(SceTransferenciaRNExceptionCode.SCE_00282);
			}else if(transferencia.getTipoMovimento().getTipoMovimentoProximo().getId().getSeq() != 0  ){

				achouProx = true;
			}
		}

		if(itemTransf.getEstoqueAlmoxarifadoOrigem().getIndControleValidade() && transferencia.getTipoMovimento().getIndDocumentoValidade()){


			List<SceDocumentoValidade> listDocValidade = this.getSceDocumentoValidadeDAO().pesquisarDocValidadeTransfAutoAlmoxarifado(transferencia.getSeq(), transferencia.getTipoMovimento().getId().getSeq(), transferencia.getTipoMovimento().getId().getComplemento(), itemTransf.getEstoqueAlmoxarifadoOrigem().getSeq());

			for(SceDocumentoValidade docValidade: listDocValidade){

				achouDocValidade = true;

				qtdeEntrada =  docValidade.getValidade().getQtdeEntrada();
				qtdeConsumo = docValidade.getValidade().getQtdeConsumida();
				qtdeDisponivel = docValidade.getValidade().getQtdeDisponivel();

				qtdeDoc = docValidade.getQuantidade();
				

				if(transferencia.getTipoMovimento().getIndQtdeEntradaValid().equals(DominioIndOperacaoBasica.DB)){

					qtdeEntrada = qtdeEntrada + qtdeDoc;

				}else if(transferencia.getTipoMovimento().getIndQtdeEntradaValid().equals(DominioIndOperacaoBasica.CR)){

					if(qtdeEntrada < qtdeDoc){

						qtdeEntrada = 0;

					}else{

						qtdeEntrada = qtdeEntrada - qtdeDoc;

					}

				}


				if(transferencia.getTipoMovimento().getIndQtdeConsumoValid().equals(DominioIndOperacaoBasica.DB)){

					qtdeConsumo = qtdeConsumo + qtdeDoc;

				}else if(transferencia.getTipoMovimento().getIndQtdeConsumoValid().equals(DominioIndOperacaoBasica.CR)){

					if(qtdeConsumo < qtdeDoc){

						qtdeConsumo = 0;

					}else{

						qtdeConsumo = qtdeConsumo - qtdeDoc;

					}

				}

				if(transferencia.getTipoMovimento().getIndQtdeDisponivel().equals(DominioIndOperacaoBasica.DB)){

					qtdeDisponivel = qtdeDisponivel + qtdeDoc;

				}else if(transferencia.getTipoMovimento().getIndQtdeDisponivel().equals(DominioIndOperacaoBasica.CR)){

					if(qtdeDisponivel < qtdeDoc){

						qtdeDisponivel = 0;

					}else{

						qtdeDisponivel = qtdeDisponivel - qtdeDoc;

					}

				}

				/**
				 * 	UPDATE SCE_VALIDADES
				 */
				docValidade.getValidade().setQtdeEntrada(qtdeEntrada);
				docValidade.getValidade().setQtdeConsumida(qtdeConsumo);
				docValidade.getValidade().setQtdeDisponivel(qtdeDisponivel);

				this.getValidadesRN().atualizar(docValidade.getValidade());



			}//Fim for

			if(achouDocValidade.equals(Boolean.FALSE)){

				//* Documento deveria ter documento validade vinculado*/
				throw new ApplicationBusinessException(SceTransferenciaRNExceptionCode.SCE_00460);

			}


		}


		if(itemTransf.getEstoqueAlmoxarifado().getIndControleValidade() && achouProx && transferencia.getTipoMovimento().getIndDocumentoValidade()){

			Short tmvSeqProx = transferencia.getTipoMovimento()!= null&transferencia.getTipoMovimento().getTipoMovimentoProximo()!=null?transferencia.getTipoMovimento().getTipoMovimentoProximo().getId().getSeq():null;
			byte tmvComplProx = transferencia.getTipoMovimento()!= null&transferencia.getTipoMovimento().getTipoMovimentoProximo()!=null?transferencia.getTipoMovimento().getTipoMovimentoProximo().getId().getComplemento():null;


			List<SceDocumentoValidade> listDocValidade = this.getSceDocumentoValidadeDAO().pesquisarDocValidadeTransfAutoAlmoxarifado(transferencia.getSeq(), tmvSeqProx, tmvComplProx, itemTransf.getEstoqueAlmoxarifado().getSeq());



			for(SceDocumentoValidade documentoValidadeProx: listDocValidade){
				//								achouDocValidade = true;

				qtdeEntradaProx =  documentoValidadeProx.getValidade().getQtdeEntrada();
				qtdeConsumoProx = documentoValidadeProx.getValidade().getQtdeConsumida();
				qtdeDisponivelProx = documentoValidadeProx.getValidade().getQtdeDisponivel();

				
				qtdeDoc = documentoValidadeProx.getQuantidade();
				

				if(transferencia.getTipoMovimento().getTipoMovimentoProximo().getIndQtdeEntradaValid().equals(DominioIndOperacaoBasica.DB)){

					qtdeEntradaProx = qtdeEntradaProx + qtdeDoc;

				}else if(transferencia.getTipoMovimento().getTipoMovimentoProximo().getIndQtdeEntradaValid().equals(DominioIndOperacaoBasica.CR)){

					if(qtdeEntradaProx < qtdeDoc){

						qtdeEntradaProx = 0;

					}else{

						qtdeEntradaProx = qtdeEntradaProx - qtdeDoc;

					}

				}


				if(transferencia.getTipoMovimento().getTipoMovimentoProximo().getIndQtdeConsumoValid().equals(DominioIndOperacaoBasica.DB)){

					qtdeConsumoProx = qtdeConsumoProx + qtdeDoc;

				}else if(transferencia.getTipoMovimento().getTipoMovimentoProximo().getIndQtdeConsumoValid().equals(DominioIndOperacaoBasica.CR)){

					if(qtdeConsumoProx < qtdeDoc){

						qtdeConsumoProx = 0;

					}else{

						qtdeConsumoProx = qtdeConsumoProx - qtdeDoc;

					}

				}

				if(transferencia.getTipoMovimento().getTipoMovimentoProximo().getIndQtdeDisponivel().equals(DominioIndOperacaoBasica.DB)){

					qtdeDisponivelProx = qtdeDisponivelProx + qtdeDoc;

				}else if(transferencia.getTipoMovimento().getTipoMovimentoProximo().getIndQtdeDisponivel().equals(DominioIndOperacaoBasica.CR)){

					if(qtdeDisponivelProx < qtdeDoc){

						qtdeDisponivelProx = 0;

					}else{

						qtdeDisponivelProx = qtdeDisponivelProx - qtdeDoc;

					}

				}



				if(qtdeEntradaProx ==0 && qtdeConsumoProx == 0 && qtdeDisponivelProx == 0){

					/**
					 * 	DELETE SCE_VALIDADES
					 */
					this.getValidadesRN().remover(documentoValidadeProx.getValidade());

				}else{


					/**
					 * 	UPDATE SCE_VALIDADES
					 */
					documentoValidadeProx.getValidade().setQtdeEntrada(qtdeEntradaProx);
					documentoValidadeProx.getValidade().setQtdeConsumida(qtdeConsumoProx);
					documentoValidadeProx.getValidade().setQtdeDisponivel(qtdeDisponivelProx);

					this.getValidadesRN().atualizar(documentoValidadeProx.getValidade());


				}


			}//fim for


			if(achouDocValidProx.equals(Boolean.FALSE)){

				//* Documento deveria ter documento validade vinculado*/
				throw new ApplicationBusinessException(SceTransferenciaRNExceptionCode.SCE_00460);

			}



		}
	}

	/**
	 * SCEK_TRF_RN.RN_TRFP_ATU_ESTORNO
	 * @param transferencia
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 * @throws BaseException
	 */
	private void atualizaEstornoEfetivacaoTransf(SceTransferencia transferencia) throws ApplicationBusinessException, BaseException {

		Short tmvSeq = transferencia.getTipoMovimento().getId().getSeq();
		Integer complemento = transferencia.getTipoMovimento().getId().getComplemento().intValue();

		//RN_TRFP_ATU_ESTORNO
		Date dtaCompetencia = this.getSceMovimentoMaterialDAO().obterDataCompetencia(tmvSeq, complemento, transferencia.getSeq());

		// busca na tabela de parametros a data de competencia vigente
		AghParametros dataCompVigente = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COMPETENCIA);

		if (dtaCompetencia == null) {

			//Não existe movimento para esta Transferência
			throw new ApplicationBusinessException(SceTransferenciaRNExceptionCode.SCE_00801);

		}

		if (!dtaCompetencia.equals(dataCompVigente.getVlrData())){

			//Só pode estornar TR  dentro do mês de competência
			throw new ApplicationBusinessException(SceTransferenciaRNExceptionCode.SCE_00801);

		} else {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			if (servidorLogado == null) {
				throw new ApplicationBusinessException(SceTransferenciaRNExceptionCode.SCE_00299);
			}

			transferencia.setServidorEstornado(servidorLogado);
			transferencia.setDtEstorno(new Date());

		}

	}




	/**
	 * ORADB PROCEDURE RN_TRFP_ATU_EFETIV
	 */
	private void atualizarEfetivacao(SceTransferencia transferencia,SceTransferencia oldTransferencia) throws BaseException{

		if (transferencia.getEfetivada()) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			if (servidorLogado == null) {

				throw new ApplicationBusinessException(SceTransferenciaRNExceptionCode.SCE_00299);

			}

			transferencia.setServidorEfetivado(servidorLogado);

		}

		transferencia.setDtEfetivacao(new Date());
	
		if(transferencia.getTipoMovimento()!=null){
			SceTipoMovimento tipoMovimento = this.getSceTipoMovimentoRN().verificarTipoMovimentoAtivo(transferencia.getTipoMovimento());
			if(tipoMovimento!=null){
				transferencia.setTipoMovimento(tipoMovimento);
			}
		}

	}


	private void validarTipoMovimentoEfetivacao(SceTransferencia transferencia,SceTransferencia oldTransferencia)
	throws ApplicationBusinessException {

		if(!transferencia.getTipoMovimento().getId().getSeq().equals(oldTransferencia.getTipoMovimento().getId().getSeq())
				|| !transferencia.getTipoMovimento().getId().getComplemento().equals(oldTransferencia.getTipoMovimento().getId().getComplemento())){


			AghParametros parametroTipoMovimento = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_TR);

			if(parametroTipoMovimento !=null && parametroTipoMovimento.getVlrNumerico()!=null){

				SceTipoMovimento sceTipoMovimentosAtivo = getSceTipoMovimentoDAO().obterSceTipoMovimentosAtivoPorSeq(parametroTipoMovimento.getVlrNumerico().shortValue());

				if(!transferencia.getTipoMovimento().getId().getSeq().equals(parametroTipoMovimento.getVlrNumerico())
						|| !transferencia.getTipoMovimento().getId().getComplemento().equals(sceTipoMovimentosAtivo.getId().getComplemento())){
					// Tipo do Movimento difere do movimento do parâmetro
					throw new ApplicationBusinessException(SceTransferenciaRNExceptionCode.SCE_00367);

				}

			}

		}


	}

	/**
	 * ORADB PROCEDURE RN_TRFP_VER_ALTERAC
	 * Atualiza 
	 * @param transferencia
	 * @throws ApplicationBusinessException 
	 */
	public void validarAtualizacao(SceTransferencia transferencia,SceTransferencia oldTransferencia) throws ApplicationBusinessException{

		//Data em que foi gerado a TRS e o servidor que gerou não poderá ser alterado

		if(!transferencia.getServidor().equals(oldTransferencia.getServidor()) || !transferencia.getDtGeracao().equals(oldTransferencia.getDtGeracao())){
			//Não alterar campos relativos a geração da Transferência
			throw new ApplicationBusinessException(SceTransferenciaRNExceptionCode.SCE_00482);

		}

		Boolean alterado = this.validarAlteracaoCamposEfetivacao(transferencia, oldTransferencia);

		if(alterado){

			throw new ApplicationBusinessException(SceTransferenciaRNExceptionCode.SCE_00481);

		}

		// TESTES DE CAMPOS ALTERADOS X SITUAÇÃO
		alterado =	this.validaAltaracao(transferencia,oldTransferencia);
		if(alterado && oldTransferencia.getEfetivada()) {

			// Quando estiver efetivado, não pode efetuar alteração
			throw new ApplicationBusinessException(SceTransferenciaRNExceptionCode.SCE_00408);

		}

		if(oldTransferencia.getEstorno()){

			if(alterado){
				// Transferência já foi estornada.
				throw new ApplicationBusinessException(SceTransferenciaRNExceptionCode.SCE_00757);
			}			

		}


	}

	/**
	 * Não alterar campos relativos a Efetivação da Transferência.
	 * @param transferencia
	 * @param oldTransferencia
	 * @throws ApplicationBusinessException
	 */
	private Boolean validarAlteracaoCamposEfetivacao(SceTransferencia transferencia, SceTransferencia oldTransferencia)
	throws ApplicationBusinessException {
		Boolean alterado = false;

		if((transferencia.getServidorEfetivado()!=null &&  oldTransferencia.getServidorEfetivado() ==null) || (oldTransferencia.getServidorEfetivado()!=null &&  transferencia.getServidorEfetivado() ==null) 
				&& transferencia.getEfetivada().equals(Boolean.FALSE)){

			alterado =true;

		}else if(transferencia.getServidorEfetivado()!=null && oldTransferencia.getServidorEfetivado() !=null ){

			if(!transferencia.getServidorEfetivado().equals(oldTransferencia.getServidorEfetivado())){

				alterado =true;

			}

		}

		if((transferencia.getDtEfetivacao()!=null &&  oldTransferencia.getDtEfetivacao() ==null) || (oldTransferencia.getDtEfetivacao()!=null &&  transferencia.getDtEfetivacao() ==null) 
				&&  transferencia.getEfetivada().equals(Boolean.FALSE)){

			alterado =true;

		}else if(transferencia.getDtEfetivacao()!=null && oldTransferencia.getDtEfetivacao() !=null){

			if(!transferencia.getDtEfetivacao().equals(oldTransferencia.getDtEfetivacao())){

				alterado =true;

			}

		}

		return alterado;


	}

	private Boolean validaAltaracao(SceTransferencia transferencia,SceTransferencia oldTransferencia) throws ApplicationBusinessException{

		Boolean alterado = false;

		if(!transferencia.getTransferenciaAutomatica().equals(oldTransferencia.getTransferenciaAutomatica())){
			alterado = true;
		}

		if(!transferencia.getAlmoxarifado().equals(oldTransferencia.getAlmoxarifado())){
			alterado = true;
		}

		if(!transferencia.getAlmoxarifadoRecebimento().equals(oldTransferencia.getAlmoxarifadoRecebimento())){
			alterado = true;
		}

		if(!transferencia.getTipoMovimento().equals(oldTransferencia.getTipoMovimento())){
			alterado = true;
		}

		/*
		 * já está sendo validado em this.validarAlteracaoCamposEfetivacao()
		 */
		//		if(!transferencia.getServidorEfetivado().equals(oldTransferencia.getServidorEfetivado())){
		//			alterado = true;
		//		}
		//
		//		if(!transferencia.getDtEfetivacao().equals(oldTransferencia.getDtEfetivacao())){
		//			alterado = true;
		//		}
		//
		if(!transferencia.getEfetivada().equals(oldTransferencia.getEfetivada())){
			//RN2 e RN3-- VERIFICA TIPO MOVIMENTO (SCE-00448 -  Transferência já efetivada.   Não pode ser ativada.)
			alterado = true;
		}



		return alterado;

	}






	/**
	 * Getters para RNs e DAOs
	 */

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public SceTransferenciaDAO getSceTransferenciaDAO(){
		return sceTransferenciaDAO;
	}

	public SceTipoMovimentosDAO getSceTipoMovimentoDAO(){
		return sceTipoMovimentosDAO;
	}

	public SceAlmoxarifadosRN getSceAlmoxarifadosRN(){
		return sceAlmoxarifadosRN;
	}

	public SceAlmoxarifadoTransferenciaAutomaticaDAO getSceAlmoxarifadoTransferenciaAutomaticaDAO(){
		return sceAlmoxarifadoTransferenciaAutomaticaDAO;
	}

	public SceTipoMovimentosRN getSceTipoMovimentoRN(){
		return sceTipoMovimentosRN;
	}

	public SceMovimentoMaterialDAO getSceMovimentoMaterialDAO(){
		return sceMovimentoMaterialDAO;
	}

	public SceItemTransferenciaDAO getSceItemTransferenciaDAO(){
		return sceItemTransferenciaDAO;
	}

	public SceMovimentoMaterialRN getSceMovimentoMaterialRN(){
		return sceMovimentoMaterialRN;
	}

	public SceItemTransferenciaRN getSceItemTransferenciaRN(){
		return sceItemTransferenciaRN;
	}

	protected SceDocumentoValidadeDAO getSceDocumentoValidadeDAO(){
		return sceDocumentoValidadeDAO;
	}

	public SceValidadesRN getValidadesRN(){
		return sceValidadesRN;
	}

	public SceDocumentoValidadeRN getSceDocumentoValidadeRN(){
		return sceDocumentoValidadeRN;
	}

	public SceLoteDocumentoRN getSceLoteDocumentoRN(){
		return sceLoteDocumentoRN;
	}
	
	public ControlarValidadeMaterialRN getControlarValidadeMaterialRN(){
		return controlarValidadeMaterialRN;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
