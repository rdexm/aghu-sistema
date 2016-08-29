package br.gov.mec.aghu.sig.custos.business;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigAtividadeCentroCustos;
import br.gov.mec.aghu.model.SigAtividadeEquipamentos;
import br.gov.mec.aghu.model.SigAtividadeInsumos;
import br.gov.mec.aghu.model.SigAtividadePessoas;
import br.gov.mec.aghu.model.SigAtividadeServicos;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.sig.custos.vo.ComposicaoAtividadeVO;
import br.gov.mec.aghu.sig.custos.vo.EquipamentoSistemaPatrimonioVO;
import br.gov.mec.aghu.sig.custos.vo.ItemComposicaoAtividadeVO;
import br.gov.mec.aghu.sig.dao.SigAtividadesDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class RelatorioComposicaoAtividadeON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(RelatorioComposicaoAtividadeON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SigAtividadesDAO sigAtividadesDAO;

@EJB
private ICustosSigFacade custosSigFacade;

	private static final long serialVersionUID = 6734568645689459L;

	public enum RelatorioComposicaoAtividadeONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_PESQUISA_ATIVIDADE_SEM_RESULTADOS
	}
	
	public List<ComposicaoAtividadeVO> montarListaComposicaoAtividades(FccCentroCustos filtoCentroCusto, SigAtividades filtroAtividade, DominioSituacao filtroSituacao) throws ApplicationBusinessException{
		
		//Buscas as atividades
		List<SigAtividades> atividades = this.getSigAtividadesDAO().buscarAtividadesRelatorio(filtoCentroCusto, filtroAtividade, filtroSituacao);
		
		if(atividades == null || atividades.isEmpty()){
			throw new ApplicationBusinessException(RelatorioComposicaoAtividadeONExceptionCode.MENSAGEM_ERRO_PESQUISA_ATIVIDADE_SEM_RESULTADOS);
		}
		
		//E monta a lista do VO
		List<ComposicaoAtividadeVO> lista = new ArrayList<ComposicaoAtividadeVO>();
		for(SigAtividades atividade : atividades){
			
			ComposicaoAtividadeVO vo = new ComposicaoAtividadeVO();
			
			vo.setNome(atividade.getNome()); //Nome
			vo.setTipo(atividade.getIndOrigemDados().getDescricao()); //Tipo
			
			//Centro de custo
			if(atividade.getListSigAtividadeCentroCustos() != null && !atividade.getListSigAtividadeCentroCustos().isEmpty()){
				
				for(SigAtividadeCentroCustos atividadeCentroCustos : atividade.getListSigAtividadeCentroCustos() ){
					if(atividadeCentroCustos.getFccCentroCustos() != null){
						if(vo.getNomeCentroCusto() == null){
							vo.setNomeCentroCusto(atividadeCentroCustos.getFccCentroCustos().getCodigoDescricao());
						}
						else{
							vo.setNomeCentroCusto(vo.getNomeCentroCusto() +", "+ atividadeCentroCustos.getFccCentroCustos().getCodigoDescricao());
						}
					}
				}
			}
			else{
				vo.setNomeCentroCusto("");	
			}
			
			//Situação
			vo.setSituacao(atividade.getIndSituacao().getDescricao());
				        
			//Itens da composição
			vo.setItensComposicao(new ArrayList<ItemComposicaoAtividadeVO>());
			this.inserirItensInsumosAtividade(atividade, vo);
			this.inserirItensPessoalAtividade(atividade, vo);
			this.inserirItensEquipamentoAtividade(atividade, vo);
			this.inserirItensServicoAtividade(atividade, vo);
			
			//Verifica se a atividade possui algum item
			if(vo.getItensComposicao().isEmpty()){
				ItemComposicaoAtividadeVO itemVO = new ItemComposicaoAtividadeVO();
				itemVO.setEspecificacao("Atividade sem itens na sua composição");
				itemVO.setQuantidade(" ");
				itemVO.setRecurso(" ");
				itemVO.setSituacao(" ");
				vo.getItensComposicao().add(itemVO);
			}
			else{
				Collections.sort(vo.getItensComposicao());
			}
			
			lista.add(vo);
		}

		Collections.sort(lista);
		
		return lista;
	}
	
	private void inserirItensInsumosAtividade(SigAtividades atividade, ComposicaoAtividadeVO vo){
		
		//Formatação de números
		Locale locBR = new Locale("pt", "BR");//Brasil 
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(locBR);
        dfs.setDecimalSeparator(',');
        DecimalFormat df = new DecimalFormat("#,###,###,###,###,##0.######", dfs);
		
		//Insumos
		for( SigAtividadeInsumos item : atividade.getListAtividadeInsumos()){
		
			ItemComposicaoAtividadeVO itemVO = new ItemComposicaoAtividadeVO();
			itemVO.setRecurso("Insumo");
			itemVO.setEspecificacao(item.getMaterial().getCodigoENome());
			
			String quantidade = "";
			if(item.getQtdeUso() != null) {
				quantidade = df.format(item.getQtdeUso())+" "+item.getUnidadeMedida().getDescricao();
			}
			if(item.getVidaUtilQtde() != null){
				quantidade = df.format(item.getVidaUtilQtde());
			}
			if(item.getVidaUtilTempo() != null){
				quantidade = df.format(item.getVidaUtilTempo())+" "+item.getDirecionadores().getNome();
			}
			
			itemVO.setQuantidade(quantidade);
			itemVO.setSituacao(item.getIndSituacao().getDescricao());
			vo.getItensComposicao().add(itemVO);
		}
		
	}
	
	private void inserirItensPessoalAtividade(SigAtividades atividade, ComposicaoAtividadeVO vo){
		//Pessoal
		for( SigAtividadePessoas item : atividade.getListAtividadePessoas()){
			
			ItemComposicaoAtividadeVO itemVO = new ItemComposicaoAtividadeVO();
			itemVO.setRecurso("Pessoal");
			itemVO.setEspecificacao(item.getSigGrupoOcupacoes().getDescricao());
			
			if(item.getTempo() != null && item.getQuantidade() != null){
				itemVO.setQuantidade(item.getQuantidade()+"x("+item.getTempoMedio().replace(".", ",")+")");
			}
			else {
				itemVO.setQuantidade(item.getQuantidade()+" "+item.getTempoMedio().replace(".", ","));
			}
			
			itemVO.setSituacao(item.getIndSituacao().getDescricao());
			vo.getItensComposicao().add(itemVO);
		}
	}
	
	private void inserirItensEquipamentoAtividade(SigAtividades atividade, ComposicaoAtividadeVO vo) throws ApplicationBusinessException{
		//Equipamento
		for( SigAtividadeEquipamentos item : atividade.getListAtividadeEquipamentos()){
			ItemComposicaoAtividadeVO itemVO = new ItemComposicaoAtividadeVO();
			itemVO.setRecurso("Equipamento");
			itemVO.setEspecificacao(item.getCodPatrimonio() + " - "+this.buscarNomeEquipamento(item.getCodPatrimonio()));
			itemVO.setQuantidade("     -");
			itemVO.setSituacao(item.getIndSituacao().getDescricao());
			vo.getItensComposicao().add(itemVO);
		}
	}
	
	private void inserirItensServicoAtividade(SigAtividades atividade, ComposicaoAtividadeVO vo){
		
		List<SigAtividadeServicos> lista =  this.getCustosSigFacade().pesquisarServicosPorSeqAtividade(atividade.getSeq());  //atividade.getListAtividadeServicos()
		
		//Serviço
		for( SigAtividadeServicos item : lista){
			
			ItemComposicaoAtividadeVO itemVO = new ItemComposicaoAtividadeVO();
			itemVO.setRecurso("Serviço");
			
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
				especificacao.append(this.getCustosSigFacade().obterAfPorId(item.getScoAfContrato().getSeq()).getNomeServico());
			}			
			
			itemVO.setEspecificacao(especificacao.toString());
			itemVO.setQuantidade("     -");
			itemVO.setSituacao(item.getIndSituacao().getDescricao());
			vo.getItensComposicao().add(itemVO);
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
	
	protected SigAtividadesDAO getSigAtividadesDAO() {
		return sigAtividadesDAO;
	}
	
	protected ICustosSigFacade getCustosSigFacade(){
		return this.custosSigFacade;
	}

}