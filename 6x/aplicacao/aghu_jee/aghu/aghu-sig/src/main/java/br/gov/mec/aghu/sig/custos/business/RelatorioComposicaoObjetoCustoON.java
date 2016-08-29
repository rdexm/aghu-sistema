package br.gov.mec.aghu.sig.custos.business;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioComposicaoObjetoCusto;
import br.gov.mec.aghu.dominio.DominioSituacaoVersoesCustos;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigAtividadeEquipamentos;
import br.gov.mec.aghu.model.SigAtividadeInsumos;
import br.gov.mec.aghu.model.SigAtividadePessoas;
import br.gov.mec.aghu.model.SigAtividadeServicos;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.model.SigObjetoCustoClientes;
import br.gov.mec.aghu.model.SigObjetoCustoComposicoes;
import br.gov.mec.aghu.model.SigObjetoCustoDirRateios;
import br.gov.mec.aghu.model.SigObjetoCustoPhis;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigObjetoCustos;
import br.gov.mec.aghu.sig.custos.vo.ComposicaoObjetoCustoVO;
import br.gov.mec.aghu.sig.custos.vo.EquipamentoSistemaPatrimonioVO;
import br.gov.mec.aghu.sig.custos.vo.ItemClienteObjetoCustoVO;
import br.gov.mec.aghu.sig.custos.vo.ItemComposicaoPorAtividadeObjetoCustoVO;
import br.gov.mec.aghu.sig.custos.vo.ItemComposicaoPorRecursoObjetoCustoVO;
import br.gov.mec.aghu.sig.custos.vo.ItemDirecionadorRateioObjetoCustoVO;
import br.gov.mec.aghu.sig.custos.vo.ItemPhiObjetoCustoVO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoVersoesDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class RelatorioComposicaoObjetoCustoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(RelatorioComposicaoObjetoCustoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private ICustosSigFacade custosSigFacade;

@Inject
private SigObjetoCustoVersoesDAO sigObjetoCustoVersoesDAO;

	private static final long serialVersionUID = 5093459854698192L;

	public enum RelatorioComposicaoObjetoCustoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_PESQUISA_OBJETO_CUSTO_VERSAO_SEM_RESULTADOS
	}
	
	public List<ComposicaoObjetoCustoVO> montarListaComposicaoObjetoCusto(FccCentroCustos filtoCentroCusto,
			SigObjetoCustos filtroObjetoCusto, DominioSituacaoVersoesCustos filtroSituacao, DominioComposicaoObjetoCusto filtroComposicaoObjetoCusto) throws ApplicationBusinessException{
		
		//Buscas as atividades
		List<SigObjetoCustoVersoes> listaObjetoCustoVersoes = this.getSigObjetoCustoVersoes().buscarObjetoCustoVersoesRelatorio(filtoCentroCusto, filtroObjetoCusto, filtroSituacao);
		
		if(listaObjetoCustoVersoes == null || listaObjetoCustoVersoes.isEmpty()){
			throw new ApplicationBusinessException(RelatorioComposicaoObjetoCustoONExceptionCode.MENSAGEM_ERRO_PESQUISA_OBJETO_CUSTO_VERSAO_SEM_RESULTADOS);
		}
		
		//E monta a lista do VO
		List<ComposicaoObjetoCustoVO> lista = new ArrayList<ComposicaoObjetoCustoVO>();
		
		//Formatação de data
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		//Formatação de números
		Locale locBR = new Locale("pt", "BR");//Brasil 
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(locBR);
        dfs.setDecimalSeparator(',');
        DecimalFormat df = new DecimalFormat("#,###,###,###,###,##0.######", dfs);
		
		
		for(SigObjetoCustoVersoes objetoCustoVersao : listaObjetoCustoVersoes){
			
			ComposicaoObjetoCustoVO vo = new ComposicaoObjetoCustoVO();
			
			vo.setNome(objetoCustoVersao.getSigObjetoCustos().getNome()); 
			vo.setTipo(objetoCustoVersao.getSigObjetoCustos().getIndTipo().name()); 
			vo.setDescricaoTipo(objetoCustoVersao.getSigObjetoCustos().getIndTipo().getDescricao()); 
			vo.setVersao(objetoCustoVersao.getNroVersao().toString());
			
			if(objetoCustoVersao.getDataInicio() != null){
				vo.setDataInicio(sdf.format(objetoCustoVersao.getDataInicio()));
			}
			
			if(objetoCustoVersao.getDataFim() != null){
				vo.setDataFim(sdf.format(objetoCustoVersao.getDataFim()));
			}
			
			vo.setSituacao(objetoCustoVersao.getIndSituacao().getDescricao());
			vo.setTipoComposicao(filtroComposicaoObjetoCusto.name());
			
			//Inicia as listas utilizadas nos subrelatórios
			vo.setItensComposicaoPorAtividade(new ArrayList<ItemComposicaoPorAtividadeObjetoCustoVO>());
			vo.setItensComposicaoPorRecurso(new ArrayList<ItemComposicaoPorRecursoObjetoCustoVO>());
			vo.setItensPhi(new ArrayList<ItemPhiObjetoCustoVO>());
			vo.setItensCliente(new ArrayList<ItemClienteObjetoCustoVO>());
			vo.setItensDirecionadorRateio(new ArrayList<ItemDirecionadorRateioObjetoCustoVO>());
			
			this.inserirItensComposicao(filtroComposicaoObjetoCusto, objetoCustoVersao, vo, df);
			this.inserirItensPhi(objetoCustoVersao, vo); 
			this.inserirItensDirecionadorRateio(objetoCustoVersao, vo, df);
			this.inserirItensCliente(objetoCustoVersao, vo, df);			
		
			Collections.sort(vo.getItensComposicaoPorAtividade());
			Collections.sort(vo.getItensComposicaoPorRecurso());
			Collections.sort(vo.getItensPhi());
			Collections.sort(vo.getItensDirecionadorRateio());
			Collections.sort(vo.getItensCliente());
			
			lista.add(vo);
		}

		Collections.sort(lista);
		
		return lista;
	}
	
	
	private void inserirItensComposicao(DominioComposicaoObjetoCusto filtroComposicaoObjetoCusto, SigObjetoCustoVersoes objetoCustoVersao, ComposicaoObjetoCustoVO vo, DecimalFormat df) throws ApplicationBusinessException{
		//Composição por atividade
		if(filtroComposicaoObjetoCusto.equals(DominioComposicaoObjetoCusto.A)){
			for(SigObjetoCustoComposicoes composicao : objetoCustoVersao.getListObjetoCustoComposicoes()){
				
				ItemComposicaoPorAtividadeObjetoCustoVO itemVo = new ItemComposicaoPorAtividadeObjetoCustoVO();
				
				if(composicao.getSigAtividades() != null){
					itemVo.setItem(composicao.getSigAtividades().getNome());
				}
				else{
					itemVo.setItem(composicao.getSigObjetoCustoVersoesCompoe().getSigObjetoCustos().getNome());
				}

				itemVo.setCentroCusto(composicao.getSigObjetoCustoVersoes().getSigObjetoCustoCctsPrincipal().getFccCentroCustos().getCodigoDescricao());
				
				if(composicao.getSigDirecionadores() != null){
					if(composicao.getNroExecucoes() != null){
						itemVo.setDirecionador(composicao.getNroExecucoes() +" "+composicao.getSigDirecionadores().getNome());
					}
					else{
						itemVo.setDirecionador(composicao.getSigDirecionadores().getNome());
					}	
				}
				
				itemVo.setSituacao(composicao.getIndSituacao().getDescricao());
				
				vo.getItensComposicaoPorAtividade().add(itemVo);
			}
		}
		//Composição por recurso
		else if(filtroComposicaoObjetoCusto.equals(DominioComposicaoObjetoCusto.R)){
			for(SigObjetoCustoComposicoes composicao : objetoCustoVersao.getListObjetoCustoComposicoes()){
				
				String situacao = composicao.getIndSituacao().getDescricao();
				String nomeDirecionador = null;
				
				if(composicao.getSigDirecionadores() != null){
					if(composicao.getNroExecucoes() != null){
						nomeDirecionador = composicao.getNroExecucoes() +" "+composicao.getSigDirecionadores().getNome();
					}
					else{
						nomeDirecionador = composicao.getSigDirecionadores().getNome();
					}	
				}
				
				if(composicao.getSigAtividades() != null){
					this.inserirItensInsumosAtividade(composicao.getSigAtividades(), nomeDirecionador, situacao, vo, df);
					this.inserirItensPessoalAtividade(composicao.getSigAtividades(), nomeDirecionador,situacao, vo);
					this.inserirItensEquipamentoAtividade(composicao.getSigAtividades(), nomeDirecionador,situacao, vo);
					this.inserirItensServicoAtividade(composicao.getSigAtividades(), nomeDirecionador,situacao, vo);
				}
			}
		}
	}
	
	private void inserirItensPhi(SigObjetoCustoVersoes objetoCustoVersao, ComposicaoObjetoCustoVO vo){

		for(SigObjetoCustoPhis  phi : objetoCustoVersao.getListObjetoCustoPhis()){
			
			ItemPhiObjetoCustoVO itemVo = new ItemPhiObjetoCustoVO();
			
			itemVo.setCodigo(phi.getFatProcedHospInternos().getSeq().toString());
			itemVo.setDescricao(phi.getFatProcedHospInternos().getDescricao());
			itemVo.setSituacao(phi.getDominioSituacao().getDescricao());
			
			vo.getItensPhi().add(itemVo);
		}
	}
	
	private void inserirItensDirecionadorRateio(SigObjetoCustoVersoes objetoCustoVersao, ComposicaoObjetoCustoVO vo, DecimalFormat df){
		
		for(SigObjetoCustoDirRateios dirRateio : objetoCustoVersao.getListObjetoCustoDirRateios()){
			
			ItemDirecionadorRateioObjetoCustoVO itemVo = new ItemDirecionadorRateioObjetoCustoVO();
			
			itemVo.setDescricao(dirRateio.getDirecionadores().getNome());
			itemVo.setPercentual(df.format(dirRateio.getPercentual()) + " %");
			itemVo.setSituacao(dirRateio.getSituacao().getDescricao());
			
			vo.getItensDirecionadorRateio().add(itemVo);
		}
	}
	
	private void inserirItensCliente(SigObjetoCustoVersoes objetoCustoVersao, ComposicaoObjetoCustoVO vo, DecimalFormat df){
		
		for(SigObjetoCustoClientes cliente : objetoCustoVersao.getListObjetoCustoClientes()){
			
			ItemClienteObjetoCustoVO itemVo = new ItemClienteObjetoCustoVO();
			
			if(cliente.getIndTodosCct() != null && cliente.getIndTodosCct()){
				itemVo.setCliente("Todos centros de custo excluindo os do grupo de obras");
				itemVo.setDirecionador(cliente.getDirecionadores().getNome());
			}
			else{
				
				if(cliente.getCentroCusto() != null){
					itemVo.setCliente(cliente.getCentroCusto().getDescricao());
				}
				else{
					itemVo.setCliente(cliente.getCentroProducao().getNome());
				}
				
				itemVo.setDirecionador(cliente.getDirecionadores().getNome());
			}
			
			if(cliente.getValor() != null){
				itemVo.setPeso(df.format(cliente.getValor()));
			}
			
			itemVo.setSituacao(cliente.getSituacao().getDescricao());
			
			vo.getItensCliente().add(itemVo);
		}
	}
	
	private void inserirItensInsumosAtividade(SigAtividades atividade, String direcionador, String situacao, ComposicaoObjetoCustoVO vo,  DecimalFormat df){
		
		for( SigAtividadeInsumos item : atividade.getListAtividadeInsumos()){
		
			ItemComposicaoPorRecursoObjetoCustoVO itemVO = new ItemComposicaoPorRecursoObjetoCustoVO();
			itemVO.setTipo("Insumo");
			itemVO.setAtividade(atividade.getNome());
			itemVO.setDirecionador(direcionador);
			itemVO.setRecurso(item.getMaterial().getCodigoENome());
			itemVO.setSituacao(situacao);
			
			String quantidade = "-";
			if(item.getQtdeUso() != null) {
				quantidade = df.format(item.getQtdeUso())+" "+item.getUnidadeMedida().getDescricao();
			}
			else if(item.getVidaUtilQtde() != null){
				quantidade = df.format(item.getVidaUtilQtde());
			}
			else if(item.getVidaUtilTempo() != null){
				quantidade = df.format(item.getVidaUtilTempo())+" " + item.getDirecionadores().getNome();
			}
		
			itemVO.setQuantidade(quantidade);
			
			vo.getItensComposicaoPorRecurso().add(itemVO);
		}
	}
	
	private void inserirItensPessoalAtividade(SigAtividades atividade, String direcionador, String situacao, ComposicaoObjetoCustoVO vo){
		
		for( SigAtividadePessoas item : atividade.getListAtividadePessoas()){
			
			ItemComposicaoPorRecursoObjetoCustoVO itemVO = new ItemComposicaoPorRecursoObjetoCustoVO();
			itemVO.setTipo("Pessoal");
			itemVO.setAtividade(atividade.getNome());
			itemVO.setDirecionador(direcionador);
			itemVO.setSituacao(situacao);
			itemVO.setRecurso(item.getSigGrupoOcupacoes().getDescricao());
			if(item.getTempo() != null){
				itemVO.setQuantidade(item.getTempoMedio());
			}
			else{
				itemVO.setQuantidade("-");
			}
			vo.getItensComposicaoPorRecurso().add(itemVO);
		}
	}
	
	private void inserirItensEquipamentoAtividade(SigAtividades atividade, String direcionador,  String situacao, ComposicaoObjetoCustoVO vo) throws ApplicationBusinessException{
		
		for( SigAtividadeEquipamentos item : atividade.getListAtividadeEquipamentos()){
			ItemComposicaoPorRecursoObjetoCustoVO itemVO = new ItemComposicaoPorRecursoObjetoCustoVO();
			itemVO.setTipo("Equipamento");
			itemVO.setAtividade(atividade.getNome());
			itemVO.setDirecionador(direcionador);
			itemVO.setSituacao(situacao);
			itemVO.setRecurso(item.getCodPatrimonio() + " - "+this.buscarNomeEquipamento(item.getCodPatrimonio()));
			itemVO.setQuantidade("-");
			vo.getItensComposicaoPorRecurso().add(itemVO);
		}
	}
	
	private void inserirItensServicoAtividade(SigAtividades atividade, String direcionador,  String situacao, ComposicaoObjetoCustoVO vo){
		
		List<SigAtividadeServicos> lista =  this.getCustosSigFacade().pesquisarServicosPorSeqAtividade(atividade.getSeq());  //atividade.getListAtividadeServicos()
		
		for( SigAtividadeServicos item : lista){
			
			ItemComposicaoPorRecursoObjetoCustoVO itemVO = new ItemComposicaoPorRecursoObjetoCustoVO();
			itemVO.setTipo("Serviço");
			itemVO.setAtividade(atividade.getNome());
			itemVO.setDirecionador(direcionador);
			itemVO.setSituacao(situacao);
						
			StringBuffer especificacao = new StringBuffer();				
			
			if(item.getScoItensContrato() != null){
				especificacao.append(item.getScoItensContrato().getContrato().getNrContrato());
				especificacao.append('/');
			}
			
			if(item.getScoAfContrato() != null){
				especificacao.append(item.getScoAfContrato().getScoContrato().getNrContrato());
				especificacao.append('/');
			}
			
			if(item.getScoItensContrato() != null){
				especificacao.append(item.getScoItensContrato().getNrItem());
			}
			
			if(item.getScoAfContrato() != null){
				especificacao.append(this.getCustosSigFacade().obterAfPorId(item.getScoAfContrato().getSeq()).getTotalItem());
			}			
			itemVO.setRecurso(especificacao.toString());
			itemVO.setQuantidade("-");
			vo.getItensComposicaoPorRecurso().add(itemVO);
		}
		
	}
	
	private String buscarNomeEquipamento(String codigoPatrimonio) throws ApplicationBusinessException{
		Integer centroCustoUniversal = 0;
		EquipamentoSistemaPatrimonioVO patrimonioVO = null;
		
		patrimonioVO = this.getCustosSigFacade().pesquisarEquipamentoSistemaPatrimonioById(codigoPatrimonio, centroCustoUniversal);
		
		if(patrimonioVO != null && patrimonioVO.getDescricao() != null){
			return patrimonioVO.getDescricao();
		}
		
		return "";
	}
	
	protected SigObjetoCustoVersoesDAO getSigObjetoCustoVersoes() {
		return sigObjetoCustoVersoesDAO;
	}
	
	protected ICustosSigFacade getCustosSigFacade(){
		return this.custosSigFacade;
	}

}