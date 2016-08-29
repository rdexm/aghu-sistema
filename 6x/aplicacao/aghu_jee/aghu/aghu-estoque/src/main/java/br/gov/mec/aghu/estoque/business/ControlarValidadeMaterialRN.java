package br.gov.mec.aghu.estoque.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioIndOperacaoBasica;
import br.gov.mec.aghu.estoque.dao.SceDocumentoValidadeDAO;
import br.gov.mec.aghu.estoque.dao.SceLoteFornecedorDAO;
import br.gov.mec.aghu.estoque.dao.SceTipoMovimentosDAO;
import br.gov.mec.aghu.estoque.dao.SceValidadeDAO;
import br.gov.mec.aghu.model.SceDocumentoValidade;
import br.gov.mec.aghu.model.SceDocumentoValidadeID;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceLote;
import br.gov.mec.aghu.model.SceLoteDocumento;
import br.gov.mec.aghu.model.SceLoteFornecedor;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.SceValidade;
import br.gov.mec.aghu.model.SceValidadeId;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
/**
 * ORADB PACKAGE SCEK_VAL_ATUALIZACAO
 * @author aghu
 *
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
@Stateless
public class ControlarValidadeMaterialRN extends BaseBusiness{

@EJB
private SceDocumentoValidadeRN sceDocumentoValidadeRN;
@EJB
private SceValidadesRN sceValidadesRN;
@EJB
private SceLoteDocumentoRN sceLoteDocumentoRN;

private static final Log LOG = LogFactory.getLog(ControlarValidadeMaterialRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SceValidadeDAO sceValidadeDAO;

@Inject
private SceLoteFornecedorDAO sceLoteFornecedorDAO;

@Inject
private SceDocumentoValidadeDAO sceDocumentoValidadeDAO;

@Inject
private SceTipoMovimentosDAO sceTipoMovimentosDAO;


	/**
	 * 
	 */
	private static final long serialVersionUID = 2020555524464424366L;

	public enum ControlarValidadeMaterialRNExceptionCode implements BusinessExceptionCode {
		SCE_00282, SCE_00637;
	}

	/**
	 * ORADB PROCEDURE SCEK_VAL_ATUALIZACAO.SCEP_TRF_ATU_VALID
	 * Controlar Validades de Materiais
	 * @param estoqueAlmoxarifadoOrigem
	 * @param estoqueAlmoxarifadoDestino
	 * @param tipoMovimento
	 * @param numeroDocumento
	 * @param quantidade
	 * @throws BaseException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength","PMD.NcssMethodCount"})
	public void atualizarValidadeTransferenciaAutomatica(SceEstoqueAlmoxarifado ealOrigem, SceEstoqueAlmoxarifado ealDestino,SceTipoMovimento tipoMovimento, Integer nroDocumento, Integer quantidade) throws BaseException{


		/* BUSCA AS OPERAÇÕES QUE DEVEM SER FEITAS SOBRE AS QUANTIDADES DAS */
		/* VALIDADES, CONFORME O TIPO DO MOVIMENTO E TIPO MOVIMENTO PROXIMO */
		Boolean achouProximo = Boolean.FALSE;
		DominioIndOperacaoBasica vOperQtdeEntrProx;
		DominioIndOperacaoBasica vOperQtdeConsProx;
		DominioIndOperacaoBasica vOperQtdeDispoProx;
		Boolean vDocValidProx;

		if(tipoMovimento == null){
			/* Tipo de Movimento não Cadastrado ou inativo */
			throw new ApplicationBusinessException(ControlarValidadeMaterialRNExceptionCode.SCE_00282);
		}

		if(tipoMovimento.getTipoMovimentoProximo() == null){

			/* Tipo de Movimento não Cadastrado ou inativo */
			throw new ApplicationBusinessException(ControlarValidadeMaterialRNExceptionCode.SCE_00282);
		}else{
			vOperQtdeEntrProx = tipoMovimento.getTipoMovimentoProximo().getIndQtdeEntradaValid();
			vOperQtdeConsProx = tipoMovimento.getTipoMovimentoProximo().getIndQtdeConsumoValid();
			vOperQtdeDispoProx = tipoMovimento.getTipoMovimentoProximo().getIndQtdeDisponivelValid();
			vDocValidProx = tipoMovimento.getTipoMovimentoProximo().getIndDocumentoValidade();
			achouProximo = Boolean.TRUE;
		}

		/* BUSCA A DATA DE VALIDADE MAIS ANTIGA */
		Integer vQtdeEntr = 0;
		Integer vQtdeCons = 0;
		Integer vQtdeDispo = 0;
		Integer vQtdeEntrProx = 0;
		Integer vQtdeConsProx = 0;
		Integer vQtdeDispoProx = 0;
		Integer vQuantidade = quantidade;
		Integer vQtdeCalculo = 0;

		Date vData;
		Integer qtdeDebito1 = 0;
		Integer qtdeDebito2 = 0;
		Integer qtdeDebito3 = 0;

		Integer vQtdeValidade = 0;

		Boolean vExisteValidade = Boolean.TRUE;


		/* CURSOR PARA BUSCAR A DATA DE VALIDADE MAIS ANTIGA */


		List<SceValidade> listValidades = getSceValidadeDAO().pesquisarValidadeEalSeqDataValidadeComQuantidadeDisponivel(ealOrigem.getSeq(), null);

		for(SceValidade validadeOld: listValidades){

			/* Tem validade na origem  */

			//scek_val_atualizacao.v_incl_val_por_doc_val := 'N';
			vQtdeEntr = validadeOld.getQtdeEntrada();
			vQtdeCons = validadeOld.getQtdeConsumida();
			vQtdeDispo = validadeOld.getQtdeDisponivel();
			vData = validadeOld.getId().getData();

			if(!(tipoMovimento.getIndQtdeDisponivelValid().equals(DominioIndOperacaoBasica.DB) && vQtdeDispo == 0) 
					|| !(tipoMovimento.getIndQtdeEntradaValid().equals(DominioIndOperacaoBasica.DB) && vQtdeEntr == 0)
					|| !(tipoMovimento.getIndQtdeConsumoValid().equals(DominioIndOperacaoBasica.DB) && vQtdeCons == 0)){


				if(tipoMovimento.getIndQtdeEntradaValid().equals(DominioIndOperacaoBasica.DB)){

					qtdeDebito1 = vQtdeEntr;

				}else{

					qtdeDebito1 = 9999999;
				}

				if(tipoMovimento.getIndQtdeConsumoValid().equals(DominioIndOperacaoBasica.DB)){

					qtdeDebito2 = vQtdeCons;

				}else{

					qtdeDebito2 = 9999999;
				}

				if(tipoMovimento.getIndQtdeDisponivelValid().equals(DominioIndOperacaoBasica.DB)){

					qtdeDebito3 = vQtdeDispo;
				}else{

					qtdeDebito3 = 9999999;
				}

				Integer qtdeDebito;
				/* Pega a menor das 3 quantidades */
				if (qtdeDebito1 < qtdeDebito2 && qtdeDebito1 < qtdeDebito3){
					qtdeDebito = qtdeDebito1;                                  
				}else if (qtdeDebito2 < qtdeDebito3){
					qtdeDebito = qtdeDebito2;  
				}else{
					qtdeDebito = qtdeDebito3; 
				}

				if(qtdeDebito < vQuantidade){

					vQuantidade = vQuantidade - qtdeDebito;
					vQtdeCalculo = qtdeDebito;

				}else{

					vQtdeCalculo = vQuantidade;
					vQuantidade = 0;

				}

				if(tipoMovimento.getIndQtdeEntradaValid().equals(DominioIndOperacaoBasica.CR)){

					vQtdeEntr = vQtdeEntr + vQtdeCalculo;

				}else if(tipoMovimento.getIndQtdeEntradaValid().equals(DominioIndOperacaoBasica.DB)){

					vQtdeEntr = vQtdeEntr - vQtdeCalculo;

				}

				if(tipoMovimento.getIndQtdeConsumoValid().equals(DominioIndOperacaoBasica.CR)){

					vQtdeCons = vQtdeCons + vQtdeCalculo;


				}else if(tipoMovimento.getIndQtdeConsumoValid().equals(DominioIndOperacaoBasica.DB)){

					vQtdeCons = vQtdeCons - vQtdeCalculo;

				}

				if(tipoMovimento.getIndQtdeDisponivelValid().equals(DominioIndOperacaoBasica.CR)){

					vQtdeDispo = vQtdeDispo + vQtdeCalculo;


				}else if(tipoMovimento.getIndQtdeDisponivelValid().equals(DominioIndOperacaoBasica.DB)){

					vQtdeDispo = vQtdeDispo - vQtdeCalculo;

				}


				/* TESTA SE ALMOX ORIGEM */
				if(ealOrigem.getIndControleValidade().equals(Boolean.TRUE)){

					validadeOld.setQtdeEntrada(vQtdeEntr);
					validadeOld.setQtdeConsumida(vQtdeCons);
					validadeOld.setQtdeDisponivel(vQtdeDispo);

					/* ALTERA AS VALIDADES */
					getValidadesRN().atualizar(validadeOld);
					getSceValidadeDAO().flush();

				}

				/* INCLUI DOCUMENTO VALIDADE QNDO TIPO DO MOVIMENTO EXIGE TAL DOCUMENTO */
				if(tipoMovimento.getIndDocumentoValidade().equals(Boolean.TRUE)){

					SceDocumentoValidadeID id = new SceDocumentoValidadeID();
					id.setTmvSeq(tipoMovimento.getId().getSeq().intValue());
					id.setTmvComplemento(tipoMovimento.getId().getComplemento().intValue());
					id.setEalSeq(ealOrigem.getSeq());
					id.setNroDocumento(nroDocumento);
					id.setData(vData);

					SceDocumentoValidade documentoValidade = new SceDocumentoValidade();
					documentoValidade.setId(id);
					documentoValidade.setQuantidade(vQtdeCalculo);
					documentoValidade.setServidor(ealOrigem.getServidor());
					documentoValidade.setDtGeracao(new Date());
					documentoValidade.setVersion(ealOrigem.getVersion());
					documentoValidade.setTipoMovimento(tipoMovimento);

					/* Insert SCE_DOCUMENTO_VALIDADES */
					getSceDocumentoValidadeRN().inserir(documentoValidade);
					getSceDocumentoValidadeDAO().flush();


					/* Atualiza Quantidade_saida da Validade na tabela de Lotes x Fornecedor, por ordem de lote mais antigo */

					vQtdeValidade = vQtdeCalculo;

					List<SceLoteFornecedor>	listLoteFornecedor = getSceLoteFornecedorDAO().pesquisarLoteFornecedorPorEalSeqLotDataValidade(ealOrigem, vData);

					for (SceLoteFornecedor loteFornecedor: listLoteFornecedor) {
					
						Integer qtdeSaida = loteFornecedor.getQuantidadeSaida()!=null?loteFornecedor.getQuantidadeSaida():0;
						Integer qtde = loteFornecedor.getQuantidade()!=null?loteFornecedor.getQuantidade():0;

						if (qtdeSaida < qtde) {

							SceLote lote = loteFornecedor.getLote();
							
							Integer vQtdeEntrLote = loteFornecedor.getQuantidade()!=null?loteFornecedor.getQuantidade():0;
							Integer vQtdeSaidaLote =  loteFornecedor.getQuantidadeSaida()!=null?loteFornecedor.getQuantidadeSaida():0;
							Integer vQtdeLote = vQtdeEntrLote  - vQtdeSaidaLote;

							if (vQtdeValidade < vQtdeLote) {

								vQtdeLote = vQtdeValidade;
								vQtdeValidade = 0;

							}

							if (vQtdeLote > 0) {

								if (vQtdeValidade >= vQtdeLote) {

									vQtdeValidade = vQtdeValidade -	 vQtdeLote;
								}


								SceLoteDocumento loteDocumentoOrigem = new SceLoteDocumento();

								loteDocumentoOrigem.setLotCodigo(lote.getId().getCodigo());
								loteDocumentoOrigem.setLotMatCodigo(lote.getId().getMatCodigo());
								loteDocumentoOrigem.setLotMcmCodigo(lote.getId().getMcmCodigo());
								loteDocumentoOrigem.setQuantidade(vQtdeLote);
								loteDocumentoOrigem.setDtValidade(vData);
								loteDocumentoOrigem.setItrTrfSeq(nroDocumento);
								loteDocumentoOrigem.setItrEalSeq(ealOrigem.getSeq());
								loteDocumentoOrigem.setTipoMovimento(tipoMovimento);
								loteDocumentoOrigem.setFornecedor(loteFornecedor.getFornecedor());

								/**
								 * INSERT INTO SCE_LOTE_X_DOCUMENTOS 
								 */
								getSceLoteDocumentoRN().inserir(loteDocumentoOrigem);
								getSceLoteFornecedorDAO().flush();


								/*  Se almox destino controla validade */
								if (ealDestino.getIndControleValidade().equals(Boolean.TRUE)) {

									/**
									 *  Insere registro Lote x Documento para Transf entrada   (Vai somar qtde saída do lote)
									 */
									
									SceLoteDocumento loteDocumentoDestino = new SceLoteDocumento();

									loteDocumentoDestino.setLotCodigo(lote.getId().getCodigo());
									loteDocumentoDestino.setLotMatCodigo(lote.getId().getMatCodigo());
									loteDocumentoDestino.setLotMcmCodigo(lote.getId().getMcmCodigo());
									loteDocumentoDestino.setQuantidade(vQtdeLote);
									loteDocumentoDestino.setDtValidade(vData);
									loteDocumentoDestino.setItrTrfSeq(nroDocumento);
									loteDocumentoDestino.setItrEalSeq(ealDestino.getSeq());
									loteDocumentoDestino.setTipoMovimento(tipoMovimento);
									loteDocumentoDestino.setFornecedor(loteFornecedor.getFornecedor());
									
									getSceLoteDocumentoRN().inserir(loteDocumentoDestino);
									getSceLoteFornecedorDAO().flush();
									
								}
								
							}
							
						}// if quant_saida < quntidade
						
					} // fim for
					
				}
				
			}

			if (achouProximo) {
				/**
				 *   BUSCAR AS INFORMAÇÕES DA VALIDADE PELA SEQUENCIA 
				 *   DA ESTQ ALMOX E PELA DATA DO PARÂMETRO            
				 */

				List<SceValidade> listValidadeDestino = getSceValidadeDAO().pesquisarValidadePorEalSeqDataValidade(ealDestino.getSeq(), vData);

				if(listValidadeDestino == null || listValidadeDestino.size() == 0){
					//Não achou Validade!

					if(!vOperQtdeEntrProx.equals(DominioIndOperacaoBasica.DB) || !vOperQtdeConsProx.equals(DominioIndOperacaoBasica.DB)
							|| !vOperQtdeDispoProx.equals(DominioIndOperacaoBasica.DB)){

						vExisteValidade = Boolean.FALSE;
						vQtdeEntrProx = 0;
						vQtdeConsProx = 0;
						vQtdeDispoProx = 0;

					}


				}else{
					// Achou Validade!

					vExisteValidade = Boolean.TRUE;
					vQtdeEntrProx = listValidadeDestino.get(0).getQtdeEntrada();
					vQtdeConsProx = listValidadeDestino.get(0).getQtdeConsumida();
					vQtdeDispoProx = listValidadeDestino.get(0).getQtdeDisponivel();

				}



				if(vOperQtdeEntrProx.equals(DominioIndOperacaoBasica.CR)){

					vQtdeEntrProx = vQtdeEntrProx + vQtdeCalculo;

				}else if(vOperQtdeEntrProx.equals(DominioIndOperacaoBasica.DB)){

					if(vQtdeEntrProx <  vQtdeCalculo){

						vQtdeEntrProx = 0;

					}else{

						vQtdeEntrProx = vQtdeEntrProx - vQtdeCalculo;

					}

				}


				if(vOperQtdeConsProx.equals(DominioIndOperacaoBasica.CR)){

					vQtdeConsProx = vQtdeConsProx + vQtdeCalculo;

				}else if(vOperQtdeConsProx.equals(DominioIndOperacaoBasica.DB)){

					if(vQtdeConsProx <  vQtdeCalculo){

						vQtdeConsProx = 0;

					}else{

						vQtdeConsProx = vQtdeConsProx - vQtdeCalculo;

					}

				}

				if(vOperQtdeDispoProx.equals(DominioIndOperacaoBasica.CR)){

					vQtdeDispoProx = vQtdeDispoProx + vQtdeCalculo;

				}else if(vOperQtdeDispoProx.equals(DominioIndOperacaoBasica.DB)){

					if(vQtdeDispoProx <  vQtdeCalculo){

						vQtdeDispoProx = 0;

					}else{

						vQtdeDispoProx = vQtdeDispoProx - vQtdeCalculo;

					}

				}

				/* TESTA SE ALMOX DESTINO */
				/* CONTROLA VALIDADE */
				if(ealDestino.getIndControleValidade().equals(Boolean.TRUE)){

					/* INCLUI OU ALTERA AS VALIDADES */

					if(vExisteValidade.equals(Boolean.FALSE)){

						SceValidadeId id = new SceValidadeId();
						id.setData(vData);
						id.setEalSeq(ealDestino.getSeq());

						SceValidade validade = new SceValidade();
						validade.setId(id);
						validade.setQtdeEntrada(vQtdeEntrProx);
						validade.setQtdeConsumida(vQtdeConsProx);
						validade.setQtdeDisponivel(vQtdeDispoProx);

						/**
						 * INSERT INTO SCE_VALIDADES
						 */
						getValidadesRN().inserir(validade);
						getSceValidadeDAO().flush();


					}else{

						SceValidadeId id = new SceValidadeId();
						id.setData(vData);
						id.setEalSeq(ealDestino.getSeq());

						SceValidade validade = getSceValidadeDAO().obterPorChavePrimaria(id);
							
						validade.setQtdeEntrada(vQtdeEntrProx);
						validade.setQtdeConsumida(vQtdeConsProx);
						validade.setQtdeDisponivel(vQtdeDispoProx);

						/**
						 * UPDATE SCE_VALIDADES
						 */
						getValidadesRN().atualizar(validade);
						getSceValidadeDAO().flush();


					}

					/* INCLUI DOCUMENTO VALIDADE QNDO TIPO DO MOVIMENTO EXIGE TAL DOCUMENTO */

					if(vDocValidProx){

						SceDocumentoValidadeID id = new SceDocumentoValidadeID();
						id.setTmvSeq(tipoMovimento.getTipoMovimentoProximo().getId().getSeq().intValue());
						id.setTmvComplemento(tipoMovimento.getTipoMovimentoProximo().getId().getComplemento().intValue());
						id.setEalSeq(ealDestino.getSeq());
						id.setNroDocumento(nroDocumento);
						id.setData(vData);

						SceDocumentoValidade documentoValidade = new SceDocumentoValidade();
						documentoValidade.setId(id);
						documentoValidade.setQuantidade(vQtdeCalculo);

						/* Insert SCE_DOCUMENTO_VALIDADES */
						getSceDocumentoValidadeRN().inserir(documentoValidade);
						getSceDocumentoValidadeDAO().flush();

					}


				}

				if(vQuantidade == 0){
					break;
				}

			}					

		}//fim for

	}


	/**
	 * ORADB PROCEDURE SCEK_VAL_ATUALIZACAO.SCEP_VAL_ATU_VALID
	 * @param estoqueAlmoxarifado
	 * @param ealDestino
	 * @param tipoMovimento
	 * @param numeroDocumento
	 * @param quantidade
	 * @throws BaseException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void atualizarQuantidadesValidade(
			final Date dataValidade,
			final SceEstoqueAlmoxarifado estoqueAlmoxarifado,
			final SceTipoMovimento tipoMovimento, 
			final Integer numeroDocumento, 
			final Integer quantidade) throws BaseException{

		Integer vQtdeEntr = 0;
		Integer vQtdeCons = 0;
		Integer vQtdeDispo = 0;
		Integer vQtdeDebito1 = 0;
		Integer vQtdeDebito2 = 0;
		Integer vQtdeDebito3 = 0;
		Integer vQtdeDebito = 0;
		Integer vQtdeCalculo = 0;
		Integer vQuantidade =0;
		Date vData;
		DominioIndOperacaoBasica vOperQtdeEntr;
		DominioIndOperacaoBasica vOperQtdeCons;
		DominioIndOperacaoBasica vOperQtdeDispo;
		Boolean vDocValid;
		Boolean vExisteValidade;
		Integer vQtdeValidade;
		SceLote lote;
		Integer vQtdeLote;
	
		if(tipoMovimento == null){
			/* Tipo de Movimento não Cadastrado ou inativo */
			throw new ApplicationBusinessException(ControlarValidadeMaterialRNExceptionCode.SCE_00282);
		}else{
			vOperQtdeEntr = tipoMovimento.getIndQtdeEntradaValid();
			vOperQtdeCons = tipoMovimento.getIndQtdeConsumoValid();
			vOperQtdeDispo = tipoMovimento.getIndQtdeDisponivelValid();
			vDocValid = tipoMovimento.getIndDocumentoValidade();
		}


		if(dataValidade == null){

			/* BUSCA A DATA DE VALIDADE MAIS ANTIGA */
			vQuantidade = quantidade;


			List<SceValidade> validades = getSceValidadeDAO().pesquisarValidadeEalSeqDataValidadeComQuantidadeDisponivel(estoqueAlmoxarifado.getSeq(), null);
			for(SceValidade validadeOld : validades){

				//scek_val_atualizacao.v_incl_val_por_doc_val := 'N';
				vQtdeEntr = validadeOld.getQtdeEntrada();
				vQtdeCons = validadeOld.getQtdeConsumida();
				vQtdeDispo = validadeOld.getQtdeDisponivel();
				vData = validadeOld.getId().getData();

				if(!((vOperQtdeDispo.equals(DominioIndOperacaoBasica.DB) && vQtdeDispo == 0) || (vOperQtdeEntr.equals(DominioIndOperacaoBasica.DB) && vQtdeEntr == 0)
						|| (vOperQtdeCons.equals(DominioIndOperacaoBasica.DB) && vQtdeCons == 0))){


					if(vOperQtdeEntr.equals(DominioIndOperacaoBasica.DB)){

						vQtdeDebito1 = vQtdeEntr;

					}else{

						vQtdeDebito1 = 9999999;
					}

					if(vOperQtdeCons.equals(DominioIndOperacaoBasica.DB)){

						vQtdeDebito2 = vQtdeCons;

					}else{

						vQtdeDebito2 = 9999999;
					}

					if(vOperQtdeDispo.equals(DominioIndOperacaoBasica.DB)){

						vQtdeDebito3 = vQtdeDispo;

					}else{

						vQtdeDebito3 = 9999999;
					}

					/* Pega a menor das 3 quantidades */
					if (vQtdeDebito1 < vQtdeDebito2 && vQtdeDebito1 < vQtdeDebito3){
						vQtdeDebito = vQtdeDebito1;                                  
					}else if (vQtdeDebito2 < vQtdeDebito3){
						vQtdeDebito = vQtdeDebito2;  
					}else{
						vQtdeDebito = vQtdeDebito3; 
					}

					if(vQtdeDebito < vQuantidade){

						vQuantidade = vQuantidade - vQtdeDebito;
						vQtdeCalculo = vQtdeDebito;

					}else{

						vQtdeCalculo = vQuantidade;
						vQuantidade = 0;

					}

					if(vOperQtdeEntr.equals(DominioIndOperacaoBasica.CR)){

						vQtdeEntr = vQtdeEntr + vQtdeCalculo;

					}else if(vOperQtdeEntr.equals(DominioIndOperacaoBasica.DB)){

						vQtdeEntr = vQtdeEntr - vQtdeCalculo;

					}

					if(vOperQtdeCons.equals(DominioIndOperacaoBasica.CR)){

						vQtdeCons = vQtdeCons + vQtdeCalculo;


					}else if(vOperQtdeCons.equals(DominioIndOperacaoBasica.DB)){

						vQtdeCons = vQtdeCons - vQtdeCalculo;

					}

					if(vOperQtdeDispo.equals(DominioIndOperacaoBasica.CR)){

						vQtdeDispo = vQtdeDispo + vQtdeCalculo;


					}else if(vOperQtdeDispo.equals(DominioIndOperacaoBasica.DB)){

						vQtdeDispo = vQtdeDispo - vQtdeCalculo;

					}

					validadeOld.setQtdeEntrada(vQtdeEntr);
					validadeOld.setQtdeConsumida(vQtdeCons);
					validadeOld.setQtdeDisponivel(vQtdeDispo);

					/* ALTERA AS VALIDADES */
					this.getSceValidadesRN().atualizar(validadeOld);

					/* Atualiza Quantidade_saida da Validade na tabela de Lotes x Fornecedor, por ordem de lote mais antigo */
					vQtdeValidade = vQtdeCalculo;

					List<SceLoteFornecedor>	listLoteFornecedor = getSceLoteFornecedorDAO().pesquisarLoteFornecedorPorEalSeqLotDataValidade(estoqueAlmoxarifado, vData);

					for(SceLoteFornecedor loteFornecedor: listLoteFornecedor){
						Integer vQtdeEntrLote = loteFornecedor.getQuantidadeSaida()!=null?loteFornecedor.getQuantidadeSaida():0;
						Integer vQtdeSaidaLote = loteFornecedor.getQuantidade()!=null?loteFornecedor.getQuantidade():0;

						if( vQtdeSaidaLote < vQtdeSaidaLote){

							lote = loteFornecedor.getLote();
							vQtdeLote = vQtdeEntrLote - vQtdeSaidaLote;

							if(vQtdeValidade < vQtdeLote){
								vQtdeLote = vQtdeValidade;
								vQtdeValidade = 0;
							}

							if(vQtdeLote > 0){

								if(vQtdeValidade >= vQtdeLote){

									vQtdeValidade = vQtdeValidade - vQtdeLote;

								}


								SceLoteDocumento loteDocumento = new SceLoteDocumento();

								loteDocumento.setLotCodigo(lote.getId().getCodigo());
								loteDocumento.setLotMatCodigo(lote.getId().getMatCodigo());
								loteDocumento.setLotMcmCodigo(lote.getId().getMcmCodigo());
								loteDocumento.setQuantidade(vQtdeLote);
								loteDocumento.setDtValidade(vData);
								loteDocumento.setItrTrfSeq(numeroDocumento);
								//loteDocumento.setEstoqueAlmoxarifado(estoqueAlmoxarifado);
								loteDocumento.setInrEalSeq(estoqueAlmoxarifado.getSeq());
								loteDocumento.setTipoMovimento(tipoMovimento);
								loteDocumento.setFornecedor(estoqueAlmoxarifado.getFornecedor());

								/**
								 * INSERT INTO SCE_LOTE_X_DOCUMENTOS 
								 */
								getSceLoteDocumentoRN().inserir(loteDocumento);

							}

						}

					}//fim for


					/* INCLUI DOCUMENTO VALIDADE QNDO TIPO DO MOVIMENTO EXIGE TAL DOCUMENTO */

					if(vDocValid){

						SceDocumentoValidadeID id = new SceDocumentoValidadeID();
						id.setTmvSeq(tipoMovimento.getId().getSeq().intValue());
						id.setTmvComplemento(tipoMovimento.getId().getComplemento().intValue());
						id.setEalSeq(estoqueAlmoxarifado.getSeq());
						id.setNroDocumento(numeroDocumento);
						id.setData(vData);

						SceDocumentoValidade documentoValidade = new SceDocumentoValidade();
						documentoValidade.setId(id);
						documentoValidade.setQuantidade(vQtdeCalculo);
						documentoValidade.setServidor(estoqueAlmoxarifado.getServidor());
						documentoValidade.setDtGeracao(new Date());
						documentoValidade.setVersion(estoqueAlmoxarifado.getVersion());
						documentoValidade.setTipoMovimento(tipoMovimento);

						/* Insert SCE_DOCUMENTO_VALIDADES */
						getSceDocumentoValidadeRN().inserir(documentoValidade); 

					}

					if(vQuantidade == 0){
						
						break;
					}
					
				}

			}//fim For

		}else{
			
			/*  BUSCAR AS INFORMAÇÕES DA VALIDADE PELA SEQUENCIA */
			/*      DA ESTQ ALMOX E PELA DATA DO PARÂMETRO       */
			SceValidade validade = null;
	 		vData = dataValidade;
			//scek_val_atualizacao.v_incl_val_por_doc_val := 'S';  ????????????????????????????????????????????????
	 		
	 		List<SceValidade> validades = getSceValidadeDAO().pesquisarValidadePorEalSeqDataValidade(estoqueAlmoxarifado.getSeq(), dataValidade);
	 		
	 		if(validades == null || validades.size() == 0){
	 			
	 			if(vOperQtdeEntr.equals(DominioIndOperacaoBasica.DB) || vOperQtdeCons.equals(DominioIndOperacaoBasica.DB) 
	 					|| vOperQtdeDispo.equals(DominioIndOperacaoBasica.DB)){
	 				
	 				/* Validade não encontrada para efetuar o débito */
	 				throw new ApplicationBusinessException(ControlarValidadeMaterialRNExceptionCode.SCE_00637);
	 				
	 			}else{
	 				
	 				vExisteValidade = Boolean.FALSE;
	 				vQtdeEntr = 0;
	 				vQtdeCons = 0;
	 				vQtdeDispo = 0;

	 			}
	 				
	 		}else{
	 			
	 			vExisteValidade = Boolean.TRUE;
	 			validade = validades.get(0);
	 			vQtdeEntr = validades.get(0).getQtdeEntrada();
	 			vQtdeCons = validades.get(0).getQtdeConsumida();
	 			vQtdeDispo = validades.get(0).getQtdeDisponivel();
	 			
	 		}
	 		
	 		
	 		
	 		if(vOperQtdeEntr.equals(DominioIndOperacaoBasica.CR)){
	 			
	 			vQtdeEntr = vQtdeEntr + quantidade;
	 		}else if(vOperQtdeEntr.equals(DominioIndOperacaoBasica.DB)){
	 			
	 			if(vQtdeEntr < quantidade){
	 				vQtdeEntr = 0;
	 			}else{
	 				
	 				vQtdeEntr = vQtdeEntr - quantidade;
	 				
	 			}
	 			
	 		}
	 		
	 		if(vOperQtdeCons.equals(DominioIndOperacaoBasica.CR)){
	 			
	 			vQtdeCons = vQtdeCons + quantidade;
	 			
	 		}else if(vOperQtdeCons.equals(DominioIndOperacaoBasica.DB)){
	 			
	 			if(vQtdeCons < quantidade){
	 				vQtdeCons = 0;
	 			}else{
	 				
	 				vQtdeCons = vQtdeCons - quantidade;
	 				
	 			}
	 		}
	 		
	 		if(vOperQtdeDispo.equals(DominioIndOperacaoBasica.CR)){
	 			
	 			vQtdeDispo = vQtdeDispo + quantidade;
	 			
	 		}else if(vOperQtdeDispo.equals(DominioIndOperacaoBasica.DB)){
	 			
	 			if(vQtdeDispo < quantidade){
	 				vQtdeDispo = 0;
	 			}else{
	 				
	 				vQtdeDispo = vQtdeDispo - quantidade;
	 				
	 			}
	 			
	 		}
	 		
	 		
	 		if(vExisteValidade && validade!=null){
	 			/* UPDATE SCE_VALIDADES */
	 			validade.setQtdeEntrada(vQtdeEntr);
	 			validade.setQtdeConsumida(vQtdeCons);
	 			validade.setQtdeDisponivel(vQtdeDispo);
	 			getValidadesRN().atualizar(validade);
	 			
	 		}else{
	 			
	 			/* INCLUI OU ALTERA AS VALIDADES */
	 			SceValidadeId id = new SceValidadeId();
	 			id.setData(vData);
	 			id.setEalSeq(estoqueAlmoxarifado.getSeq());
	 			
	 			validade = new SceValidade();
	 			validade.setId(id);
	 			validade.setQtdeEntrada(vQtdeEntr);
	 			validade.setQtdeConsumida(vQtdeCons);
	 			validade.setQtdeDisponivel(vQtdeDispo);
	 			
	 			/**
	 			 * INSERT INTO SCE_VALIDADES
	 			 */
	 			getValidadesRN().inserir(validade);
	 			
	 		}
	 		
	 	}
		
	}

	/**
	 * ORADB PROCEDURE SCEP_VAL_EST_VALID
	 * Obs. Por questões de centralização essa PROCEDURE GLOBAL foi migrada dentro desta RN
	 * @param tipoMovimentoSeq
	 * @param tipoMovimentoComplemento
	 * @param numeroDocumento
	 * @param ealSeq
	 * @param unidadeMedidaDocumento
	 * @param unidadeMedidaEstoque
	 * @param codigoMaterial
	 * @throws BaseException
	 */
	public void atualizarValidadesEstoque(
			final Short tipoMovimentoSeq,
			final Byte tipoMovimentoComplemento, 
			final Integer numeroDocumento, 
			final Integer ealSeq,
			final ScoUnidadeMedida unidadeMedidaDocumento, 
			final ScoUnidadeMedida unidadeMedidaEstoque,
			final ScoMaterial codigoMaterial) throws BaseException {

		SceTipoMovimento tipoMovimento = getSceTipoMovimentosDAO().obterSceTipoMovimentosSeqComplemento(tipoMovimentoSeq,tipoMovimentoComplemento);
		Integer qtdeDoc = 0;
		Integer qtdeEntr = 0;
		Integer qtdeCons = 0;
		Integer qtdeDispo = 0;
		Date dtValidDoc = null;
		Integer seqEstqAlmox = 0;

		if(tipoMovimento.getIndDocumentoValidade()){
			List<SceDocumentoValidade> documentoValidades = getSceDocumentoValidadeDAO().pesquisarDocValidadeTransfAutoAlmoxarifado(numeroDocumento, tipoMovimentoSeq, tipoMovimentoComplemento, ealSeq);
			for(SceDocumentoValidade documentoValidade :documentoValidades){

				qtdeEntr = documentoValidade.getValidade().getQtdeEntrada();
				qtdeCons = documentoValidade.getValidade().getQtdeConsumida();				
				qtdeDispo = documentoValidade.getValidade().getQtdeDisponivel();	
				dtValidDoc = documentoValidade.getValidade().getId().getData();
				seqEstqAlmox = documentoValidade.getValidade().getEstoqueAlmoxarifado().getSeq();

				qtdeDoc = documentoValidade.getQuantidade();
				
				if(tipoMovimento.getIndQtdeEntradaValid().equals(DominioIndOperacaoBasica.DB)){
					qtdeEntr += qtdeDoc;
				}else if(tipoMovimento.getIndQtdeEntradaValid().equals(DominioIndOperacaoBasica.CR)){
					if(qtdeEntr < qtdeDoc){
						qtdeEntr = 0;
					}else{
						qtdeEntr -= qtdeDoc;
					}
				}

				if(tipoMovimento.getIndQtdeConsumoValid().equals(DominioIndOperacaoBasica.DB)){
					qtdeCons += qtdeDoc;
				}else if(tipoMovimento.getIndQtdeConsumoValid().equals(DominioIndOperacaoBasica.CR)){
					if(qtdeCons < qtdeDoc){
						qtdeCons = 0;
					}else{
						qtdeCons -= qtdeDoc;
					}
				}

				if(tipoMovimento.getIndQtdeDisponivelValid().equals(DominioIndOperacaoBasica.DB)){
					qtdeDispo += qtdeDoc;
				}else if(tipoMovimento.getIndQtdeDisponivelValid().equals(DominioIndOperacaoBasica.CR)){
					if(qtdeDispo < qtdeDoc){
						qtdeDispo = 0;
					}else{
						qtdeDispo -= qtdeDoc;
					}
				}

				List<SceValidade> validades = getSceValidadeDAO().pesquisarValidadeEalSeqDataValidadeComQuantidadeDisponivel(seqEstqAlmox, dtValidDoc);
				for(SceValidade valid : validades){
					valid.setQtdeEntrada(qtdeEntr);
					valid.setQtdeConsumida(qtdeCons);
					valid.setQtdeDisponivel(qtdeDispo);
					getSceValidadesRN().atualizar(valid);
				}

			}
		}
	}

	/**
	 * Getters para RNs e DAOs
	 */

	protected SceValidadesRN getSceValidadesRN(){
		return sceValidadesRN;
	}

	protected SceLoteFornecedorDAO getSceLoteFornecedorDAO(){
		return sceLoteFornecedorDAO;
	}

	protected SceValidadeDAO getSceValidadeDAO(){
		return sceValidadeDAO;
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

	protected SceTipoMovimentosDAO getSceTipoMovimentosDAO(){
		return sceTipoMovimentosDAO;
	}

	protected SceDocumentoValidadeDAO getSceDocumentoValidadeDAO(){
		return sceDocumentoValidadeDAO;
	}

}
