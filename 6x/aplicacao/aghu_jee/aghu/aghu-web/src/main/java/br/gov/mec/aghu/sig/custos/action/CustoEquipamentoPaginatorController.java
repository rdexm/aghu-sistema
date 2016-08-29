package br.gov.mec.aghu.sig.custos.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioTipoVisaoAnalise;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;
import br.gov.mec.aghu.sig.custos.vo.EquipamentoSistemaPatrimonioVO;
import br.gov.mec.aghu.sig.custos.vo.VisualizarAnaliseTabCustosObjetosCustoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class CustoEquipamentoPaginatorController extends ActionController {

	private List<VisualizarAnaliseTabCustosObjetosCustoVO> lista;

	private static final long serialVersionUID = 7984910026070995423L;

	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;
	
	@EJB
	private ICustosSigFacade custosSigFacade;

	private Integer seqCompetencia;
	private Integer seqObjetoCustoVersao;
	private Integer seqCentroCusto;
	private DominioTipoVisaoAnalise tipoVisaoAnaliseItens;

	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciarAbaObjetoCusto(Integer seqCompetencia, Integer seqObjetoCustoVersao, Integer seqCentroCusto) {
		this.seqObjetoCustoVersao = seqObjetoCustoVersao;
		this.seqCentroCusto = seqCentroCusto;
		this.iniciarAba(seqCompetencia, DominioTipoVisaoAnalise.OBJETO_CUSTO);
	}

	public void iniciarAbaCentroCusto(Integer seqCompetencia, Integer seqCentroCusto) {
		this.seqCentroCusto = seqCentroCusto;
		this.iniciarAba(seqCompetencia, DominioTipoVisaoAnalise.CENTRO_CUSTO);
	}
	
	private void iniciarAba(Integer seqCompetencia, DominioTipoVisaoAnalise tipo ){
		this.seqCompetencia = seqCompetencia;
		this.tipoVisaoAnaliseItens = tipo;
		this.pesquisar();
	}

	
	private boolean verificarServicoPatrimonioEstaOnline() {
		try{
			String codigo= "0000";
			Integer cc = 0;
			this.custosSigFacade.pesquisarEquipamentoSistemaPatrimonioById(codigo,cc);
			return true;
			
		}catch (Exception e) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SERVICO_PATRIMONIO_FORA");
			return false;
		}
	}	
	
	private void carregarDescricoesEquipamentos(){
		
		if(this.verificarServicoPatrimonioEstaOnline()){
			
			List<String> codigos = new ArrayList<String>();
			for(VisualizarAnaliseTabCustosObjetosCustoVO vo : lista){
				vo.setCodigo(vo.getDescricao());//Passa o código para o campo mais coerente do VO
				vo.setDescricao("");
				codigos.add(vo.getCodigo());
				
			}
			
			Map<String,String>  descricoes = this.buscarDescricaoEquipamentos(codigos);
			
			for(VisualizarAnaliseTabCustosObjetosCustoVO vo : lista){
				if(descricoes.containsKey(vo.getCodigo())){
					vo.setDescricao(descricoes.get(vo.getCodigo()));
				}
			}
			Collections.sort(lista);
		}
	}
	
	private Map<String,String> buscarDescricaoEquipamentos(List<String> codigos){
	
		try {
			List<EquipamentoSistemaPatrimonioVO> retorno = custosSigFacade.pesquisarEquipamentosSistemaPatrimonioById(codigos);
			Map<String, String> descricoes = new HashMap<String, String>();
			if(retorno == null){
				return descricoes;		
			}
			
			for (EquipamentoSistemaPatrimonioVO equipamentoSistemaPatrimonioVO : retorno) {
				descricoes.put(equipamentoSistemaPatrimonioVO.getCodigo(), equipamentoSistemaPatrimonioVO.getDescricao());
			}
			return descricoes;		
		} catch (ApplicationBusinessException e) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_SERVICO_PATRIMONIO_FALHA");
			return null;
		}
	}
	
	public void pesquisar(){
		this.lista = this.custosSigCadastrosBasicosFacade.buscarMovimentosEquipamentos(this.seqCompetencia, this.seqObjetoCustoVersao,  this.seqCentroCusto, this.tipoVisaoAnaliseItens);
		this.carregarDescricoesEquipamentos();
	}

	public Integer getSeqCompetencia() {
		return seqCompetencia;
	}

	public void setSeqCompetencia(Integer seqCompetencia) {
		this.seqCompetencia = seqCompetencia;
	}

	public Integer getSeqObjetoCustoVersao() {
		return seqObjetoCustoVersao;
	}

	public void setSeqObjetoCustoVersao(Integer seqObjetoCustoVersao) {
		this.seqObjetoCustoVersao = seqObjetoCustoVersao;
	}

	public Integer getSeqCentroCusto() {
		return seqCentroCusto;
	}

	public void setSeqCentroCusto(Integer seqCentroCusto) {
		this.seqCentroCusto = seqCentroCusto;
	}

	public DominioTipoVisaoAnalise getTipoVisaoAnaliseItens() {
		return tipoVisaoAnaliseItens;
	}

	public void setTipoVisaoAnaliseItens(DominioTipoVisaoAnalise tipoVisaoAnaliseItens) {
		this.tipoVisaoAnaliseItens = tipoVisaoAnaliseItens;
	}
	
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> getLista() {
		return lista;
	}

	public void setLista(List<VisualizarAnaliseTabCustosObjetosCustoVO> lista) {
		this.lista = lista;
	}
}
