package br.gov.mec.aghu.compras.pac.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoCondicaoPagamentoProposDAO;
import br.gov.mec.aghu.compras.dao.ScoFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoItemLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoMarcaComercialDAO;
import br.gov.mec.aghu.compras.dao.ScoMarcaModeloDAO;
import br.gov.mec.aghu.compras.dao.ScoMaterialDAO;
import br.gov.mec.aghu.compras.dao.ScoParcelasPagamentoDAO;
import br.gov.mec.aghu.compras.dao.ScoParecerAvaliacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoParecerOcorrenciaDAO;
import br.gov.mec.aghu.compras.dao.ScoPareceresMateriaisDAO;
import br.gov.mec.aghu.compras.dao.ScoPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.pac.vo.FormaPagamentoPropostaVO;
import br.gov.mec.aghu.compras.pac.vo.FornecedoresParticipantesVO;
import br.gov.mec.aghu.compras.pac.vo.ItemQuadroPropostasVO;
import br.gov.mec.aghu.compras.pac.vo.PropostaFornecedorVO;
import br.gov.mec.aghu.compras.pac.vo.RelatorioQuadroPropostasLicitacaoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.dominio.DominioMotivoDesclassificacaoItemProposta;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMarcaModelo;
import br.gov.mec.aghu.model.ScoMarcaModeloId;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoParcelasPagamento;
import br.gov.mec.aghu.model.ScoParecerAvaliacao;
import br.gov.mec.aghu.model.ScoParecerOcorrencia;
import br.gov.mec.aghu.model.ScoPareceresMateriais;

@Stateless
public class RelatorioQuadroPropostasProvisisorioON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(RelatorioQuadroPropostasProvisisorioON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoCondicaoPagamentoProposDAO scoCondicaoPagamentoProposDAO;

@Inject
private ScoMaterialDAO scoMaterialDAO;

@Inject
private ScoMarcaComercialDAO scoMarcaComercialDAO;

@Inject
private ScoLicitacaoDAO scoLicitacaoDAO;

@Inject
private ScoParecerAvaliacaoDAO scoParecerAvaliacaoDAO;

@Inject
private ScoMarcaModeloDAO scoMarcaModeloDAO;

@Inject
private ScoParecerOcorrenciaDAO scoParecerOcorrenciaDAO;

@Inject
private ScoItemPropostaFornecedorDAO scoItemPropostaFornecedorDAO;

@Inject
private ScoPropostaFornecedorDAO scoPropostaFornecedorDAO;

@Inject
private ScoItemLicitacaoDAO scoItemLicitacaoDAO;

@Inject
private ScoFornecedorDAO scoFornecedorDAO;

@Inject
private ScoParcelasPagamentoDAO scoParcelasPagamentoDAO;

@Inject
private ScoPareceresMateriaisDAO scoPareceresMateriaisDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5255715180937543745L;

	public List<RelatorioQuadroPropostasLicitacaoVO> pesquisarQuadroProvisorioItensPropostas(Set<Integer> listaNumPac, Short numeroInicial, 
			Short numeroFinal, String listaItens) {
		List<RelatorioQuadroPropostasLicitacaoVO> listaQuadroProvisorioItensPropostas = new ArrayList<RelatorioQuadroPropostasLicitacaoVO>();
		
		for (Integer numPac : listaNumPac){		
			RelatorioQuadroPropostasLicitacaoVO quadroProvisorioItensPropostas = new RelatorioQuadroPropostasLicitacaoVO();
	
			ScoLicitacao licitacao = getScoLicitacaoDAO().obterPorChavePrimaria(numPac);
	
			quadroProvisorioItensPropostas.setModalidade(licitacao.getModalidadeLicitacao().getDescricao());
			quadroProvisorioItensPropostas.setPac(numPac);
			
			
			
			if (licitacao.getDthrAberturaProposta() != null) {
				SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
				quadroProvisorioItensPropostas.setDtAbertura(fmt.format(licitacao.getDthrAberturaProposta()));
			}
			
			if (licitacao.getHrAberturaProposta() != null){
				StringBuilder horaFormatada = new StringBuilder(String.format("%04d", licitacao.getHrAberturaProposta()));
				horaFormatada.insert(2, ":");
				quadroProvisorioItensPropostas.setHora(horaFormatada.toString());				
			}
	
			quadroProvisorioItensPropostas.setListaItemPropostas(pesquisarItensPropostasProvisorias(numPac, numeroInicial, numeroFinal, listaItens));
			
			quadroProvisorioItensPropostas.setListaFornecedorParticipante(pesquisarListaFornecedorParticipante(quadroProvisorioItensPropostas.getListaItemPropostas()));
	
			listaQuadroProvisorioItensPropostas.add(quadroProvisorioItensPropostas);
		}
		
		return listaQuadroProvisorioItensPropostas;
	}
	
	public List<FornecedoresParticipantesVO> pesquisarListaFornecedorParticipante(List<ItemQuadroPropostasVO> listaItemPropostas){
		Set<Integer> listaNumFornecedor = buscarListaNumFornecedor(listaItemPropostas);
		
		List<ScoFornecedor> listaFornecedorParticipante = buscarListaFornecedorParticipante(listaNumFornecedor);
		
		List<FornecedoresParticipantesVO> listaFornecedoresParticipantesVO = preencherListaFornecedoresParticipantesVO(listaFornecedorParticipante);
		
		return listaFornecedoresParticipantesVO;
	}

	private List<FornecedoresParticipantesVO> preencherListaFornecedoresParticipantesVO(List<ScoFornecedor> listaFornecedorParticipante){
		List<FornecedoresParticipantesVO> listaFornecedoresParticipantesVO = new ArrayList<FornecedoresParticipantesVO>();
		
		for (ScoFornecedor fornecedor : listaFornecedorParticipante){
			FornecedoresParticipantesVO fornecedoresParticipantesVO = new FornecedoresParticipantesVO();
			
			fornecedoresParticipantesVO.setRazaoSocial(fornecedor.getRazaoSocial());
			
			if (fornecedor.getCgc() != null){
				fornecedoresParticipantesVO.setCgcCnpj(fornecedor.getCgc());
			} else {
				fornecedoresParticipantesVO.setCgcCnpj(fornecedor.getCpf());
			}
			
			fornecedoresParticipantesVO.setDtValidadeFgts(formatarData(fornecedor.getDtValidadeFgts()));
			
			fornecedoresParticipantesVO.setDtValidadeInss(formatarData(fornecedor.getDtValidadeInss()));
			
			fornecedoresParticipantesVO.setDtValidadeRecFed(formatarData(fornecedor.getDtValidadeRecFed()));

			fornecedoresParticipantesVO.setDtValidadeCNDT(formatarData(fornecedor.getDtValidadeCNDT()));
			
			if(fornecedor.getIndAfe() != null){
				if (fornecedor.getIndAfe().equals("A")){
					fornecedoresParticipantesVO.setIndAfe("Ativo");
				} else {
					fornecedoresParticipantesVO.setIndAfe("Inativo");
				}
			}
			
			fornecedoresParticipantesVO.setDtValidadeCrc(formatarData(fornecedor.getDtValidadeCrc()));			
			
			fornecedoresParticipantesVO.setDtValidadeAvsm(formatarData(fornecedor.getDtValidadeAvsm()));			

			fornecedoresParticipantesVO.setDtValidadeRecEst(formatarData(fornecedor.getDtValidadeRecEst()));

			fornecedoresParticipantesVO.setDtValidadeRecMun(formatarData(fornecedor.getDtValidadeRecMun()));
			
			fornecedoresParticipantesVO.setDtValidadeBal(formatarData(fornecedor.getDtValidadeBal()));
			
			listaFornecedoresParticipantesVO.add(fornecedoresParticipantesVO);
		}
		
		return listaFornecedoresParticipantesVO;
	}

	private String formatarData(Date data){
		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
		
		String dataFormada = null;
		
		if (data != null){
			dataFormada = fmt.format(data);
		}
		
		return dataFormada;
	}
	
	private List<ScoFornecedor> buscarListaFornecedorParticipante(Set<Integer> listaNumFornecedor){
		 List<ScoFornecedor> listaFornecedorParticipante = new ArrayList<ScoFornecedor>();
		
		for (Integer numFornecedor : listaNumFornecedor){
			ScoFornecedor fornecedorParticipante = getScoFornecedorDAO().obterFornecedorPorNumero(numFornecedor);
			listaFornecedorParticipante.add(fornecedorParticipante);
		}
		
		return listaFornecedorParticipante;
	}
	
	private Set<Integer> buscarListaNumFornecedor(List<ItemQuadroPropostasVO> listaItemPropostas){
		Set<Integer> listaNumFornecedor = new HashSet<Integer>();
		
		for (ItemQuadroPropostasVO itemProposta : listaItemPropostas){
			for (PropostaFornecedorVO proposta : itemProposta.getListaProposta()){
				if (!listaNumFornecedor.contains(proposta.getNumeroFornecedor())){				
					listaNumFornecedor.add(proposta.getNumeroFornecedor());
				}
			}
		}
		
		return listaNumFornecedor;		
	}

	private List<ItemQuadroPropostasVO> pesquisarItensPropostasProvisorias(
			Integer numPac, Short numeroInicial, Short numeroFinal, String listaItens) {

		// Pesquisa da lista de itens do PAC
		List<Object[]> resultItensProposta = getScoItemLicitacaoDAO().pesquisarItemQuadroPropostas(numPac, numeroInicial, numeroFinal, listaItens);

		// Preenchimento da lista de VO's de itens
		List<ItemQuadroPropostasVO> listaItemQuadroPropostasVO = preencherItemQuadroPropostasVO(resultItensProposta);

		// Consulta das propostas de fornecedores para cada item
		for (ItemQuadroPropostasVO itemProposta : listaItemQuadroPropostasVO) {
			List<Object[]> resultPropostas = getScoPropostaFornecedorDAO().pesquisarPropostaPorLicitacaoEItem(numPac, itemProposta.getNumItem());
			
			List<PropostaFornecedorVO> listaPropostasFornecedorVO = preencherPropostaFornecedorVO(resultPropostas, itemProposta.getTipoSolicitacao());
			
			itemProposta.setListaProposta(listaPropostasFornecedorVO);

			pesquisarParecerPropostas(listaPropostasFornecedorVO, numPac, itemProposta.getNumItem().shortValue(), itemProposta.getNumMaterialServico());
		}

		return listaItemQuadroPropostasVO;
	}

	// Set do parecer em cada proposta de fornecedor
	private void pesquisarParecerPropostas(
			List<PropostaFornecedorVO> listaPropostasFornecedorVO,
			Integer numPac, Short numItem, Integer matCodigo) {

		List<ScoParecerOcorrencia> listaParecerOcorrencias = null;
		List<ScoParecerAvaliacao> listaParecerAvaliacao = null;
		String descricaoParecer = null;
		boolean testaMarcaModelo;

		for (PropostaFornecedorVO propostaFornecedor : listaPropostasFornecedorVO) {

			/*ScoItemPropostaFornecedorId idItemProposta = new ScoItemPropostaFornecedorId();
			idItemProposta.setPfrLctNumero(numPac);
			idItemProposta.setPfrFrnNumero(propostaFornecedor.getNumeroFornecedor());
			idItemProposta.setNumero(numItem);*/

			ScoItemPropostaFornecedor itemProposta = getScoItemPropostaFornecedorDAO()
					.obterItemPorLicitacaoFornecedorNumeroItem(propostaFornecedor.getNumeroFornecedor(),numPac, numItem);
			
			if (itemProposta != null){

				ScoMaterial material = getScoMaterialDAO().obterPorChavePrimaria(matCodigo);
	
				
				if (itemProposta.getMarcaComercial() != null
						&& itemProposta.getModeloComercial() != null) {
					testaMarcaModelo = true;
				} else {
					testaMarcaModelo = false;
				}
				
			
				List<ScoPareceresMateriais> listaScoPareceresMateriais = scoPareceresMateriaisDAO.pesquisarParecesMateriaisPorMaterialMarca(material.getCodigo(), propostaFornecedor.getCodigoMarca());
				
				// Consulta no pareceres em estrutura antiga caso nao ache vai para estrutura nova
				if (listaScoPareceresMateriais != null
						&& listaScoPareceresMateriais.size() > 0) {
					
					ScoPareceresMateriais pareceresMateriais = listaScoPareceresMateriais.get(0);
	
					descricaoParecer = (pareceresMateriais.getParecer().equals("F") ? "Favorável" : "Desfavorável") + " " + pareceresMateriais.getId().getPtcOptCodigo() + "." + pareceresMateriais.getId().getPtcNroSubPasta();
					propostaFornecedor.setParecer(descricaoParecer);
				}
				else {
					// Consulta no parecer de ocorrência
					listaParecerOcorrencias = getScoParecerOcorrenciaDAO()
							.pesquisarParecerOcorrenciaItemProposta(itemProposta, material, testaMarcaModelo);
		
					if (listaParecerOcorrencias != null
							&& listaParecerOcorrencias.size() > 0) {
						ScoParecerOcorrencia parecerOcorrencia = listaParecerOcorrencias
								.get(listaParecerOcorrencias.size() - 1);
		
						descricaoParecer = parecerOcorrencia.getParecerOcorrencia().getDescricao();
		
						if (parecerOcorrencia.getParecerMaterial() != null
								&& parecerOcorrencia.getParecerMaterial().getOrigemParecerTecnico() != null
								&& parecerOcorrencia.getParecerMaterial().getOrigemParecerTecnico().getCodigo() != null) {
							descricaoParecer = descricaoParecer.concat(" " + parecerOcorrencia.getParecerMaterial().getOrigemParecerTecnico().getCodigo());
						}
						
						if (parecerOcorrencia.getParecerMaterial() != null && parecerOcorrencia.getParecerMaterial().getNumeroSubPasta() != null){
							descricaoParecer = descricaoParecer.concat("." + parecerOcorrencia.getParecerMaterial().getNumeroSubPasta().toString());
						}
		
						propostaFornecedor.setParecer(descricaoParecer);
					}
		
					// Caso não encontrado, consulta do parecer da avaliação
					if (descricaoParecer == null) {
						listaParecerAvaliacao = getScoParecerAvaliacaoDAO().pesquisarParecerAvaliacaoItemProposta(itemProposta, material, testaMarcaModelo);
		
						if (listaParecerAvaliacao != null
								&& listaParecerAvaliacao.size() > 0) {
							ScoParecerAvaliacao parecerAvaliacao = listaParecerAvaliacao
									.get(listaParecerAvaliacao.size() - 1);
		
							descricaoParecer = parecerAvaliacao.getParecerGeral().getDescricao(); 
							
							if (parecerAvaliacao.getParecerMaterial() != null 
									&& parecerAvaliacao.getParecerMaterial().getOrigemParecerTecnico() != null 
									&& parecerAvaliacao.getParecerMaterial().getOrigemParecerTecnico().getCodigo() != null){
								descricaoParecer = descricaoParecer.concat(" " + parecerAvaliacao.getParecerMaterial().getOrigemParecerTecnico().getCodigo());
							}
							
							if (parecerAvaliacao.getParecerMaterial() != null && parecerAvaliacao.getParecerMaterial().getNumeroSubPasta() != null){
								descricaoParecer = descricaoParecer.concat("." + parecerAvaliacao.getParecerMaterial().getNumeroSubPasta().toString());
							}
		
							propostaFornecedor.setParecer(descricaoParecer);
						}
					}
				}
			}
			propostaFornecedor.setListaFormaPagamento(consultarFormasPagamentoProposta(propostaFornecedor.getNumeroFornecedor(), numPac, numItem));			
		}
	}
	
	private List<FormaPagamentoPropostaVO> consultarFormasPagamentoProposta(Integer nroFornecedor, Integer numPac, Short numItem){
		List<ScoCondicaoPagamentoPropos> listaCondicoesPagamento = getScoCondicaoPagamentoProposDAO().obterCondicaoPagamentoProposta(nroFornecedor, numPac, numItem);
		
		if (listaCondicoesPagamento == null || listaCondicoesPagamento.size() == 0) {
			listaCondicoesPagamento = getScoCondicaoPagamentoProposDAO().obterCondicaoPagamentoProposta(nroFornecedor, numPac, null);
		}
		
		List<FormaPagamentoPropostaVO> listaFormaPagamento = new ArrayList<FormaPagamentoPropostaVO>();
		
		boolean novaFormaPagamento = false;
		
		for (ScoCondicaoPagamentoPropos condicaoPagamento : listaCondicoesPagamento){
			FormaPagamentoPropostaVO formaPagamento = new FormaPagamentoPropostaVO();

			formaPagamento.setCondicao(condicaoPagamento.getFormaPagamento().getDescricao());
			formaPagamento.setAcrescimo(condicaoPagamento.getPercAcrescimo());
			formaPagamento.setDesconto(condicaoPagamento.getPercDesconto());

			List<ScoParcelasPagamento> listaParcelas = this.getScoParcelasPagamentoDAO().obterParcelasPgtoProposta(condicaoPagamento.getNumero());
			
			if (listaParcelas != null) {
				for (ScoParcelasPagamento parcela : listaParcelas){
					if (novaFormaPagamento){
						FormaPagamentoPropostaVO novaFPgto = new FormaPagamentoPropostaVO();
						
						novaFPgto.setPrazo(parcela.getPrazo().intValue());
						novaFPgto.setPercentual(parcela.getPercPagamento());
						novaFPgto.setValor(parcela.getValorPagamento());
						
						listaFormaPagamento.add(novaFPgto);									
					} else {
						formaPagamento.setPrazo(parcela.getPrazo().intValue());
						formaPagamento.setPercentual(parcela.getPercPagamento());
						formaPagamento.setValor(parcela.getValorPagamento());
						
						listaFormaPagamento.add(formaPagamento);
					}
					
					novaFormaPagamento = true;
				}
			}
			if (!novaFormaPagamento){
				listaFormaPagamento.add(formaPagamento);
			}
		}
		
		return listaFormaPagamento;
	}

	private List<PropostaFornecedorVO> preencherPropostaFornecedorVO(
			List<Object[]> resultList, String tipoSolicitacao) {
		List<PropostaFornecedorVO> listaPropostaFornecedorVO = new ArrayList<PropostaFornecedorVO>();

		for (Object[] object : resultList) {
			PropostaFornecedorVO propostasFornecedorVO = new PropostaFornecedorVO();

			propostasFornecedorVO.setNomeFornecedor((String) object[0]);
			Long cnpjCpf = (Long) object[1];
			propostasFornecedorVO.setCnpjCpf(cnpjCpf.toString());
			Integer seqp = (Integer) object[9];
			Integer mcmCodigo = (Integer) object[10];
			if (mcmCodigo == null) {
				mcmCodigo = (Integer) object[11];
			}
			String marca = retornaMarca(mcmCodigo);
			propostasFornecedorVO.setCodigoMarca(mcmCodigo);
			propostasFornecedorVO.setMarca(marca);
			String modelo = retornaModelo(mcmCodigo, seqp);
			propostasFornecedorVO.setModelo(modelo);
			propostasFornecedorVO.setEmbalagem((String) object[3]);
			propostasFornecedorVO.setApresentacao((String) object[12]);
			Integer fatorConversao = (Integer) object[5];
			propostasFornecedorVO.setFator(fatorConversao);
			Long quantidade = (Long) object[4];
			propostasFornecedorVO.setQtdOfertada(quantidade.intValue());
			BigDecimal valorEmbalagem = (BigDecimal) object[2];
			propostasFornecedorVO.setValorEmbalagem(valorEmbalagem);
			propostasFornecedorVO.setOrigem((String) object[7]);
			propostasFornecedorVO.setMoeda((String) object[8]);
			Long qtdConvertida = quantidade * fatorConversao;
			propostasFornecedorVO.setQtdConvertida(qtdConvertida.intValue());
			BigDecimal valorUnitario = (BigDecimal) CoreUtil.nvl(valorEmbalagem,BigDecimal.ZERO);
			valorUnitario = valorUnitario.divide(new BigDecimal(fatorConversao), 4, RoundingMode.HALF_UP);
			propostasFornecedorVO.setValorUnitario(valorUnitario);
			propostasFornecedorVO.setNumeroFornecedor((Integer) object[13]);
			propostasFornecedorVO.setNumeroIdItem((Short) object[14]);
			DominioMotivoDesclassificacaoItemProposta motivoDesclassificacao = (DominioMotivoDesclassificacaoItemProposta) object[15];
			if (motivoDesclassificacao != null){
				propostasFornecedorVO.setMotivoDesclassificacao(motivoDesclassificacao.getDescricao());
			}
			propostasFornecedorVO.setJustificativaAutorizacaoUsuario((String) object[16]);
			
			propostasFornecedorVO.setTipoSolicitacao(tipoSolicitacao);
			
			listaPropostaFornecedorVO.add(propostasFornecedorVO);
		}

		return listaPropostaFornecedorVO;
	}

	private String retornaMarca(Integer mcmCodigo) {
		if (mcmCodigo == null) {
			return null;
		}

		ScoMarcaComercial marca = getScoMarcaComercialDAO()
				.obterMarcaComercialPorCodigo(mcmCodigo);

		if (marca != null) {
			return marca.getDescricao();
		} else {
			return null;
		}
	}

	private String retornaModelo(Integer mcmCodigo, Integer seqp) {
		if (mcmCodigo == null || seqp == null) {
			return null;
		}

		ScoMarcaModeloId marcaModeloId = new ScoMarcaModeloId();
		marcaModeloId.setMcmCodigo(mcmCodigo);
		marcaModeloId.setSeqp(seqp);

		ScoMarcaModelo marcaModelo = getScoMarcaModeloDAO()
				.obterPorChavePrimaria(marcaModeloId);

		if (marcaModelo != null) {
			return marcaModelo.getDescricao();
		} else {
			return null;
		}
	}

	private List<ItemQuadroPropostasVO> preencherItemQuadroPropostasVO(
			List<Object[]> resultList) {
		List<ItemQuadroPropostasVO> listaItemQuadroPropostasVO = new ArrayList<ItemQuadroPropostasVO>();

		for (Object[] object : resultList) {
			ItemQuadroPropostasVO itemQuadroProposta = new ItemQuadroPropostasVO();

			Short numItem = (Short) object[0];
			itemQuadroProposta.setNumItem(numItem.intValue());
			itemQuadroProposta.setNumSolicitacao((Integer) object[1]);
			itemQuadroProposta.setDescSolicitacao((String) object[2]);
			itemQuadroProposta.setNumMaterialServico((Integer) object[3]);
//			Integer qtdSolicitada = (Integer) object[4];
//			itemQuadroProposta.setQtdSolicitada(Integer.valueOf(qtdSolicitada.toString()));
			itemQuadroProposta.setUnidadeSolicitada((String) object[5]);
			itemQuadroProposta.setNomeMaterialServico((String) object[6]);
			itemQuadroProposta.setDescMaterialServico((String) object[7]);
			itemQuadroProposta.setIndMenorPreco((String) object[8]);
			itemQuadroProposta.setTipoSolicitacao((String) object[9]);
			
			String mS = (String) object[9];
			
			if (mS.equals("SC")){
				Long qtdSolicitada = object[4] != null ? (Long) object[4] : 0;
				itemQuadroProposta.setQtdSolicitada(Integer.valueOf(qtdSolicitada.toString()));
			} else {
				itemQuadroProposta.setQtdSolicitada((Integer) object[4]);
			}

			listaItemQuadroPropostasVO.add(itemQuadroProposta);
		}
		
		Collections.sort(listaItemQuadroPropostasVO);

		return listaItemQuadroPropostasVO;
	}

	private ScoLicitacaoDAO getScoLicitacaoDAO() {
		return scoLicitacaoDAO;
	}

	private ScoItemLicitacaoDAO getScoItemLicitacaoDAO() {
		return scoItemLicitacaoDAO;
	}

	private ScoPropostaFornecedorDAO getScoPropostaFornecedorDAO() {
		return scoPropostaFornecedorDAO;
	}

	private ScoMarcaComercialDAO getScoMarcaComercialDAO() {
		return scoMarcaComercialDAO;
	}

	private ScoMarcaModeloDAO getScoMarcaModeloDAO() {
		return scoMarcaModeloDAO;
	}

	private ScoItemPropostaFornecedorDAO getScoItemPropostaFornecedorDAO() {
		return scoItemPropostaFornecedorDAO;
	}

	private ScoParecerOcorrenciaDAO getScoParecerOcorrenciaDAO() {
		return scoParecerOcorrenciaDAO;
	}

	private ScoParecerAvaliacaoDAO getScoParecerAvaliacaoDAO() {
		return scoParecerAvaliacaoDAO;
	}

	private ScoMaterialDAO getScoMaterialDAO() {
		return scoMaterialDAO;
	}

	private ScoCondicaoPagamentoProposDAO getScoCondicaoPagamentoProposDAO(){
		return scoCondicaoPagamentoProposDAO;
	}
	
	private ScoFornecedorDAO getScoFornecedorDAO(){
		return scoFornecedorDAO;
	}
	
	private ScoParcelasPagamentoDAO getScoParcelasPagamentoDAO() {
		return scoParcelasPagamentoDAO;
	}

}
