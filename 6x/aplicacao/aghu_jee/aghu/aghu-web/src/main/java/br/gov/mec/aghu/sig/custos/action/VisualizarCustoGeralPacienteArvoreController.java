package br.gov.mec.aghu.sig.custos.action;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.model.SigCalculoAtdProcedimentos;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;
import br.gov.mec.aghu.sig.custos.vo.CalculoAtendimentoPacienteVO;
import br.gov.mec.aghu.sig.custos.vo.DetalheConsumoVO;

public class VisualizarCustoGeralPacienteArvoreController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -508570827472786825L;

	@EJB
	private ICustosSigFacade custosSigFacade;
	
	private List<CalculoAtendimentoPacienteVO> listaCustoGeral;
	private List<CalculoAtendimentoPacienteVO> listaCustoGeralCentroCusto;
	private List<CalculoAtendimentoPacienteVO> listaCustoGeralCentroProducao;
	private List<CalculoAtendimentoPacienteVO> listaCustoGeralEspecialidade;
	private List<CalculoAtendimentoPacienteVO> listaEspecialidade;
	private List<CalculoAtendimentoPacienteVO> listaCustoGeralEquipeMedica;
	private List<CalculoAtendimentoPacienteVO> listaEquipeMedica;
	private List<CalculoAtendimentoPacienteVO> listaCustoGeralInternacao;
	private List<SigCalculoAtdProcedimentos>   listaProcedimentosInternacao;
	private List<CalculoAtendimentoPacienteVO> listaCentroCusto;
	private List<CalculoAtendimentoPacienteVO> listaReceitaGeral;
	private List<CalculoAtendimentoPacienteVO> listaReceitaCustoGeralCentroProducao;
	private List<CalculoAtendimentoPacienteVO> listaReceitaEspecialidade;
	private List<CalculoAtendimentoPacienteVO> listaReceitaEquipeMedica;
	
	private BigDecimal total, totalCentroCusto, totalEspecialidade, totalEquipeMedica;
	private BigDecimal somatorioReceitaCentroCusto, somatorioReceita, somatorioReceitaEquipeMedica, somatorioReceitaEspecialidade;
	private BigDecimal totalGeralCentroCusto = BigDecimal.ZERO;
	private BigDecimal totalGeralEspecialidade = BigDecimal.ZERO;
	private BigDecimal totalGeralEquipeMedica = BigDecimal.ZERO;
	
	private BigDecimal totalReceitaCentroCusto = BigDecimal.ZERO;
	private BigDecimal totalReceitaEspecialidade = BigDecimal.ZERO;
	private BigDecimal totalReceitaEquipeMedica = BigDecimal.ZERO;
	
	private Integer prontuario, pmuSeq, codigoCentroCusto, atdSeq;
	private List<Integer> seqCategorias;
	
	private String totalGeralCentroCustoStr, totalGeralEquipeMedicaStr, totalGeralEspecialidadeStr;
	private String totalReceitaCentroCustoStr, totalReceitaEquipeMedicaStr, totalReceitaEspecialidadeStr;
	private static final NumberFormat FORMATTER = new DecimalFormat("#,##0.00");
	
	private List<DetalheConsumoVO> listaDetalheConsumo;
	private CalculoAtendimentoPacienteVO vo;
	private Integer cctCodigo;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation, true);
	}
	
	private String valorFormatado(BigDecimal valor){
		if (valor == null) {
			return " - ";
		}
		return "R$ " + FORMATTER.format(valor);
	}

	public void resetar() {
		somatorioReceitaCentroCusto = BigDecimal.ZERO;
		somatorioReceita = BigDecimal.ZERO;
		somatorioReceitaEquipeMedica = BigDecimal.ZERO;
		somatorioReceitaEspecialidade = BigDecimal.ZERO;
		totalGeralCentroCusto = BigDecimal.ZERO;
		totalGeralEspecialidade = BigDecimal.ZERO;
		totalGeralEquipeMedica = BigDecimal.ZERO;
		totalReceitaCentroCusto = BigDecimal.ZERO;
		totalReceitaEspecialidade = BigDecimal.ZERO;
		totalReceitaEquipeMedica = BigDecimal.ZERO;
		total = BigDecimal.ZERO;
		totalCentroCusto = BigDecimal.ZERO; 
		totalEspecialidade = BigDecimal.ZERO; 
		totalEquipeMedica = BigDecimal.ZERO;
		
		prontuario = null;
		pmuSeq = null;
		codigoCentroCusto = null;
		seqCategorias = new ArrayList<Integer>();
		
		listaCustoGeral = new ArrayList<CalculoAtendimentoPacienteVO>();
		listaCustoGeralCentroCusto = new ArrayList<CalculoAtendimentoPacienteVO>();
		listaCustoGeralCentroProducao = new ArrayList<CalculoAtendimentoPacienteVO>();
		listaCustoGeralEspecialidade = new ArrayList<CalculoAtendimentoPacienteVO>();
		listaEspecialidade = new ArrayList<CalculoAtendimentoPacienteVO>();
		listaCustoGeralEquipeMedica = new ArrayList<CalculoAtendimentoPacienteVO>();
		listaEquipeMedica = new ArrayList<CalculoAtendimentoPacienteVO>();
		listaCustoGeralInternacao = new ArrayList<CalculoAtendimentoPacienteVO>();
		listaProcedimentosInternacao = new ArrayList<SigCalculoAtdProcedimentos>();
		listaCentroCusto = new ArrayList<CalculoAtendimentoPacienteVO>();
		listaReceitaGeral = new ArrayList<CalculoAtendimentoPacienteVO>();
		listaReceitaCustoGeralCentroProducao = new ArrayList<CalculoAtendimentoPacienteVO>();
		listaReceitaEspecialidade = new ArrayList<CalculoAtendimentoPacienteVO>();
		listaReceitaEquipeMedica = new ArrayList<CalculoAtendimentoPacienteVO>();
	}
	
	public void inicializarAbas() {
		if(prontuario != null) {			
			//aba geral
			listaCustoGeral = this.getCustosSigFacade().buscarCustosPacienteVisaoGeral(prontuario, pmuSeq, atdSeq);
			listaReceitaGeral = this.getCustosSigFacade().pesquisarReceitaGeral(prontuario, pmuSeq, atdSeq);  
			calcularTotais();
			custoGeralCentroProducao();
			
			//aba centro de custo
			this.totalGeralCentroCusto = this.getCustosSigFacade().buscarCustoTotal(prontuario, pmuSeq, false, false, atdSeq, seqCategorias);
			this.totalReceitaCentroCusto = this.getCustosSigFacade().buscarValorTotalReceita(prontuario, pmuSeq, false, false, atdSeq, seqCategorias);
			listaCentroCusto = this.getCustosSigFacade().buscarCentrosCustoVisaoGeral(prontuario, pmuSeq, codigoCentroCusto, seqCategorias, atdSeq);
			if(!listaCentroCusto.isEmpty()) {
				custoGeralCentroCusto(listaCentroCusto.get(0).getCodCentroCusto());
			}
			
			//aba especialidade
			this.totalGeralEspecialidade = this.getCustosSigFacade().buscarCustoTotal(prontuario, pmuSeq, true, false, atdSeq, seqCategorias);
			this.totalReceitaEspecialidade = this.getCustosSigFacade().buscarValorTotalReceita(prontuario, pmuSeq, true, false, atdSeq, seqCategorias);
			listaEspecialidade = this.getCustosSigFacade().buscarEspecialidades(prontuario, pmuSeq, atdSeq, seqCategorias);
			
			
			//aba equipe medica
			this.totalGeralEquipeMedica = this.getCustosSigFacade().buscarCustoTotal(prontuario, pmuSeq, false, true, atdSeq, seqCategorias);
			this.totalReceitaEquipeMedica = this.getCustosSigFacade().buscarValorTotalReceita(prontuario, pmuSeq, false, true, atdSeq, seqCategorias);
			listaEquipeMedica = this.getCustosSigFacade().buscarEquipesMedicas(prontuario, pmuSeq, atdSeq, seqCategorias);
			
			//aba internacao
			listaCustoGeralInternacao = this.getCustosSigFacade().buscarCustosPacienteInternacao(prontuario, pmuSeq, atdSeq);
			listaProcedimentosInternacao = this.getCustosSigFacade().buscarProcedimentosPacienteInternacao(prontuario, pmuSeq, atdSeq);
		}
	}
	
	public List<CalculoAtendimentoPacienteVO> custoGeralEspecialidade(Short espSeq) {
		listaCustoGeralEspecialidade = new ArrayList<CalculoAtendimentoPacienteVO>();
		
		if(prontuario != null) {
			listaCustoGeralEspecialidade = this.getCustosSigFacade().buscarCustosPacienteVisaoGeralCentroCusto(prontuario, pmuSeq, null, espSeq, atdSeq, Boolean.TRUE, seqCategorias);
		}
		calcularTotaisEspecialidade();
		this.calcularReceitaPorEspecialidade(listaCustoGeralEspecialidade);
		
		return listaCustoGeralEspecialidade;
	}
	
	public List<CalculoAtendimentoPacienteVO> custoGeralEquipeMedica(Integer matriculaResp, Short vinCodigoResp) {
		listaCustoGeralEquipeMedica = new ArrayList<CalculoAtendimentoPacienteVO>();
		
		if(prontuario != null) {
			listaCustoGeralEquipeMedica = this.getCustosSigFacade().buscarCustosPacienteEquipeMedica(prontuario, pmuSeq, matriculaResp, vinCodigoResp, atdSeq, seqCategorias);
		}
		calcularTotaisEquipeMedica();
		this.calcularReceitaPorEquipeMedica(listaCustoGeralEquipeMedica);
		
		return listaCustoGeralEquipeMedica;
	}
	
	private void calcularTotaisEquipeMedica() {
		totalEquipeMedica = BigDecimal.ZERO;
		for(CalculoAtendimentoPacienteVO item: listaCustoGeralEquipeMedica) {
			totalEquipeMedica = totalEquipeMedica.add(item.getCustoTotal());
			this.calcularCustoUnitario(item);
		}
	}

	public void visualizarDetalheConsumo(){
		if(vo != null){
			this.listaDetalheConsumo = this.custosSigFacade.listarDetalheConsumo(atdSeq, pmuSeq, cctCodigo, vo.getObjNome());
		}
		else{
			this.listaDetalheConsumo = new ArrayList<DetalheConsumoVO>();
		}
	}
	
	/**
	 * Método chamado no accordionPanel
	 * 
	 * @param codigoCentroCusto
	 * @return custo geral por centro de custo informado
	 */
	public List<CalculoAtendimentoPacienteVO> custoGeralCentroCusto(Integer codigoCentroCusto) {
		listaCustoGeralCentroCusto = new ArrayList<CalculoAtendimentoPacienteVO>();
		
		if(prontuario != null) {
			if(seqCategorias != null && seqCategorias.size()>1){
				listaCustoGeralCentroCusto = this.getCustosSigFacade().buscarCustosPacienteVisaoGeral(prontuario, pmuSeq, seqCategorias, atdSeq);
				this.calcularReceitaPorCentroCusto(listaCustoGeralCentroCusto);
			}
			if(seqCategorias != null && !seqCategorias.isEmpty()) {
				listaCustoGeralCentroCusto = this.getCustosSigFacade().buscarCustosPacienteVisaoGeralCentroCustoCategoria(prontuario, pmuSeq, codigoCentroCusto, seqCategorias, atdSeq);
				this.calcularReceitaPorCentroCusto(listaCustoGeralCentroCusto);
			} else {
				listaCustoGeralCentroCusto = this.getCustosSigFacade().buscarCustosPacienteVisaoGeralCentroCusto(prontuario, pmuSeq, codigoCentroCusto, null, atdSeq, Boolean.FALSE, seqCategorias);
				this.calcularReceitaPorCentroCusto(listaCustoGeralCentroCusto);
			}
			calcularTotaisCentroCusto();
		}
		
		return listaCustoGeralCentroCusto;
	}
	
	/**
	 * Método chamado no accordionPanel
	 * 
	 * @param codigoCentroCusto
	 * @return custo geral por centro de custo informado
	 */
	public List<CalculoAtendimentoPacienteVO> custoGeralAbaGeral(Integer codigoCentroCusto) {
		listaCustoGeralCentroCusto = new ArrayList<CalculoAtendimentoPacienteVO>();
		
		if(prontuario != null) {
			if(seqCategorias != null && seqCategorias.size()>1){
				listaCustoGeralCentroCusto = this.getCustosSigFacade().buscarCustosPacienteVisaoGeralCentroCustoCategoria(prontuario, pmuSeq, codigoCentroCusto, seqCategorias, atdSeq);
			}
			if(seqCategorias != null && !seqCategorias.isEmpty()) {
				listaCustoGeralCentroCusto = this.getCustosSigFacade().buscarCustosPacienteVisaoGeralCentroCustoCategoria(prontuario, pmuSeq, codigoCentroCusto, seqCategorias, atdSeq);
			} else {
				listaCustoGeralCentroCusto = this.getCustosSigFacade().buscarCustosPacienteVisaoGeralCentroCusto(prontuario, pmuSeq, codigoCentroCusto, null, atdSeq, Boolean.FALSE, seqCategorias);
			}
			calcularTotaisCentroCusto();
		}
		
		return listaCustoGeralCentroCusto;
	}
	
	public List<CalculoAtendimentoPacienteVO> custoGeralCentroProducao() {
		listaCustoGeralCentroProducao = new ArrayList<CalculoAtendimentoPacienteVO>();
		
		if(prontuario != null && codigoCentroCusto != null) {
			listaCustoGeralCentroProducao = this.getCustosSigFacade().buscarCustosPacienteVisaoGeralCentroCustoCategoria(prontuario, pmuSeq, codigoCentroCusto, seqCategorias, atdSeq);
		}
		
		for(CalculoAtendimentoPacienteVO item: listaCustoGeralCentroProducao){
			this.calcularCustoUnitario(item);
		}

		this.calcularReceitaPorCentroProducao(listaCustoGeralCentroProducao);
		
		return listaCustoGeralCentroProducao;
	}
	
	private void calcularTotaisCentroCusto() {
		totalCentroCusto = BigDecimal.ZERO;
		for(CalculoAtendimentoPacienteVO item: listaCustoGeralCentroCusto) {
			totalCentroCusto = totalCentroCusto.add(item.getCustoTotal());
			this.calcularCustoUnitario(item);
		}
	}
	
	
	private void calcularReceitaPorCentroCusto(List<CalculoAtendimentoPacienteVO> lista) {
		List<Integer> listaCtcSeq = new ArrayList<Integer>();
		somatorioReceitaCentroCusto = BigDecimal.ZERO;
		for(CalculoAtendimentoPacienteVO item: lista){
			if(item.getCtcSeq()!=null){
				listaCtcSeq.add(item.getCtcSeq());	
			}
		}
		listaReceitaCustoGeralCentroProducao = this.getCustosSigFacade().pesquisarReceitaPorCentroCusto(prontuario, pmuSeq, atdSeq, listaCtcSeq);	
		for(CalculoAtendimentoPacienteVO item: lista){
			for(CalculoAtendimentoPacienteVO receita: listaReceitaCustoGeralCentroProducao){
				if(item.getCentroProducaoFormatado().equals(receita.getCentroProducaoFormatado())&&receita.getReceitaTotal()!=null&&item.getReceitaTotal()==null){
					item.setReceitaTotal(receita.getReceitaTotal());
				} else if(item.getCentroProducaoFormatado().equals(receita.getCentroProducaoFormatado())&&receita.getReceitaTotal()!=null&&item.getReceitaTotal()!=null){
					item.setReceitaTotal(item.getReceitaTotal().add(receita.getReceitaTotal()));
				}	
			}
			if(item.getReceitaTotal()!=null){
				somatorioReceitaCentroCusto = somatorioReceitaCentroCusto.add(item.getReceitaTotal());	
			}
		}
	}
	
	private void calcularReceitaPorCentroProducao(List<CalculoAtendimentoPacienteVO> lista) {
		List<Integer> listaCtcSeq = new ArrayList<Integer>();
		for(CalculoAtendimentoPacienteVO item: lista){
			listaCtcSeq.add(item.getCtcSeq());
		}
		listaReceitaCustoGeralCentroProducao = this.getCustosSigFacade().pesquisarReceitaPorCentroProducao(prontuario, pmuSeq, atdSeq, listaCtcSeq);	
		for(CalculoAtendimentoPacienteVO item: lista){
			for(CalculoAtendimentoPacienteVO receita: listaReceitaCustoGeralCentroProducao){
				if(item.getCentroProducaoFormatado().equals(receita.getCentroProducaoFormatado())&&receita.getReceitaTotal()!=null&&item.getReceitaTotal()==null){
					item.setReceitaTotal(receita.getReceitaTotal());
				} else if(item.getCentroProducaoFormatado().equals(receita.getCentroProducaoFormatado())&&receita.getReceitaTotal()!=null&&item.getReceitaTotal()!=null){
					item.setReceitaTotal(item.getReceitaTotal().add(receita.getReceitaTotal()));
				}	
			}
		}
	}
	
	private void calcularReceitaPorEspecialidade(List<CalculoAtendimentoPacienteVO> lista) {
		listaReceitaEspecialidade = this.getCustosSigFacade().pesquisarReceitaPorEspecialidade(prontuario, pmuSeq, atdSeq);	
		somatorioReceitaEspecialidade = BigDecimal.ZERO;
		for(CalculoAtendimentoPacienteVO item: lista){
			for(CalculoAtendimentoPacienteVO receita: listaReceitaEspecialidade){
				if(item.getEspSeq().equals(receita.getEspSeq())&&item.getCtcSeq().equals(receita.getCtcSeq())){
					item.setReceitaTotal(receita.getReceitaTotal());
					break;
				}	
			}
			if(item.getReceitaTotal()!=null){
				somatorioReceitaEspecialidade = somatorioReceitaEspecialidade.add(item.getReceitaTotal());	
			}
		}
	}
	
	private void calcularReceitaPorEquipeMedica(List<CalculoAtendimentoPacienteVO> lista) {
		listaReceitaEquipeMedica = this.getCustosSigFacade().pesquisarReceitaPorEquipeMedica(prontuario, pmuSeq, atdSeq);
		somatorioReceitaEquipeMedica = BigDecimal.ZERO;
		for(CalculoAtendimentoPacienteVO item: lista){
			for(CalculoAtendimentoPacienteVO receita: listaReceitaEquipeMedica){
				if(item.getMatriculaRespEquipe().equals(receita.getMatriculaRespEquipe())&&item.getVinCodigoRespEquipe().equals(receita.getVinCodigoRespEquipe())&&item.getCtcSeq().equals(receita.getCtcSeq())){
					item.setReceitaTotal(receita.getReceitaTotal());
					break;
				}	
			}
			if(item.getReceitaTotal()!=null){
				somatorioReceitaEquipeMedica = somatorioReceitaEquipeMedica.add(item.getReceitaTotal());	
			}
	
		}
	}
	
	private void calcularTotaisEspecialidade() {
		totalEspecialidade = BigDecimal.ZERO;
		for(CalculoAtendimentoPacienteVO item: listaCustoGeralEspecialidade) {
			totalEspecialidade = totalEspecialidade.add(item.getCustoTotal());
			this.calcularCustoUnitario(item);
		}
	}
	
	private void calcularTotais() {
		total = BigDecimal.ZERO;
		somatorioReceita = BigDecimal.ZERO;
		for(CalculoAtendimentoPacienteVO item: listaCustoGeral) {
			total = total.add(item.getCustoTotal());
			this.calcularCustoUnitario(item);
			for(CalculoAtendimentoPacienteVO receita: listaReceitaGeral){
				if(item.getCtcSeq().equals(receita.getCtcSeq())){
					item.setReceitaTotal(receita.getReceitaTotal());
					break;
				}
			}
			if(item.getReceitaTotal()!=null){
				somatorioReceita = somatorioReceita.add(item.getReceitaTotal());	
			}
		}
	}
	
	private void calcularCustoUnitario(CalculoAtendimentoPacienteVO item) {
		if(item.getQuantidade()!=BigDecimal.ZERO){
			item.setCustoUnitario(item.getCustoTotal().divide(item.getQuantidade(),MathContext.DECIMAL128));	
		} else {
			item.setCustoUnitario(item.getCustoTotal());
		}
	}
	
	
	public List<CalculoAtendimentoPacienteVO> getListaCustoGeral() {
		return listaCustoGeral;
	}

	public void setListaCustoGeral(List<CalculoAtendimentoPacienteVO> listaCustoGeral) {
		this.listaCustoGeral = listaCustoGeral;
	}

	public List<CalculoAtendimentoPacienteVO> getListaCustoGeralCentroCusto() {
		return listaCustoGeralCentroCusto;
	}

	public void setListaCustoGeralCentroCusto(
			List<CalculoAtendimentoPacienteVO> listaCustoGeralCentroCusto) {
		this.listaCustoGeralCentroCusto = listaCustoGeralCentroCusto;
	}

	public List<CalculoAtendimentoPacienteVO> getListaCustoGeralCentroProducao() {
		return listaCustoGeralCentroProducao;
	}

	public List<CalculoAtendimentoPacienteVO> getListaCustoGeralEquipeMedica() {
		return listaCustoGeralEquipeMedica;
	}

	public void setListaCustoGeralEquipeMedica(
			List<CalculoAtendimentoPacienteVO> listaCustoGeralEquipeMedica) {
		this.listaCustoGeralEquipeMedica = listaCustoGeralEquipeMedica;
	}

	public List<CalculoAtendimentoPacienteVO> getListaEquipeMedica() {
		return listaEquipeMedica;
	}

	public void setListaEquipeMedica(
			List<CalculoAtendimentoPacienteVO> listaEquipeMedica) {
		this.listaEquipeMedica = listaEquipeMedica;
	}

	public void setListaCustoGeralCentroProducao(
			List<CalculoAtendimentoPacienteVO> listaCustoGeralCentroProducao) {
		this.listaCustoGeralCentroProducao = listaCustoGeralCentroProducao;
	}

	public List<CalculoAtendimentoPacienteVO> getListaCustoGeralEspecialidade() {
		return listaCustoGeralEspecialidade;
	}

	public void setListaCustoGeralEspecialidade(
			List<CalculoAtendimentoPacienteVO> listaCustoGeralEspecialidade) {
		this.listaCustoGeralEspecialidade = listaCustoGeralEspecialidade;
	}

	public List<CalculoAtendimentoPacienteVO> getListaEspecialidade() {
		return listaEspecialidade;
	}

	public void setListaEspecialidade(
			List<CalculoAtendimentoPacienteVO> listaEspecialidade) {
		this.listaEspecialidade = listaEspecialidade;
	}

	public List<CalculoAtendimentoPacienteVO> getListaCustoGeralInternacao() {
		return listaCustoGeralInternacao;
	}

	public void setListaCustoGeralInternacao(
			List<CalculoAtendimentoPacienteVO> listaCustoGeralInternacao) {
		this.listaCustoGeralInternacao = listaCustoGeralInternacao;
	}

	public ICustosSigFacade getCustosSigFacade() {
		return custosSigFacade;
	}

	public List<CalculoAtendimentoPacienteVO> getListaCentroCusto() {
		return listaCentroCusto;
	}

	public void setListaCentroCusto(
			List<CalculoAtendimentoPacienteVO> listaCentroCusto) {
		this.listaCentroCusto = listaCentroCusto;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public BigDecimal getTotalGeralCentroCusto() {
		return totalGeralCentroCusto;
	}

	public void setTotalGeralCentroCusto(BigDecimal totalGeralCentroCusto) {
		this.totalGeralCentroCusto = totalGeralCentroCusto;
	}

	public BigDecimal getTotalCentroCusto() {
		return totalCentroCusto;
	}

	public void setTotalCentroCusto(BigDecimal totalCentroCusto) {
		this.totalCentroCusto = totalCentroCusto;
	}

	public BigDecimal getTotalEspecialidade() {
		return totalEspecialidade;
	}

	public void setTotalEspecialidade(BigDecimal totalEspecialidade) {
		this.totalEspecialidade = totalEspecialidade;
	}

	public BigDecimal getTotalGeralEspecialidade() {
		return totalGeralEspecialidade;
	}

	public void setTotalGeralEspecialidade(BigDecimal totalGeralEspecialidade) {
		this.totalGeralEspecialidade = totalGeralEspecialidade;
	}

	public BigDecimal getTotalEquipeMedica() {
		return totalEquipeMedica;
	}

	public void setTotalEquipeMedica(BigDecimal totalEquipeMedica) {
		this.totalEquipeMedica = totalEquipeMedica;
	}

	public BigDecimal getTotalGeralEquipeMedica() {
		return totalGeralEquipeMedica;
	}

	public void setTotalGeralEquipeMedica(BigDecimal totalGeralEquipeMedica) {
		this.totalGeralEquipeMedica = totalGeralEquipeMedica;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getPmuSeq() {
		return pmuSeq;
	}

	public void setPmuSeq(Integer pmuSeq) {
		this.pmuSeq = pmuSeq;
	}

	public List<Integer> getSeqCategorias() {
		return seqCategorias;
	}

	public Integer getCodigoCentroCusto() {
		return codigoCentroCusto;
	}

	public void setCodigoCentroCusto(Integer codigoCentroCusto) {
		this.codigoCentroCusto = codigoCentroCusto;
	}

	public void setSeqCategorias(List<Integer> seqCategorias) {
		this.seqCategorias = seqCategorias;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public BigDecimal getSomatorioReceitaCentroCusto() {
		return somatorioReceitaCentroCusto;
	}

	public void setSomatorioReceitaCentroCusto(
			BigDecimal somatorioReceitaCentroCusto) {
		this.somatorioReceitaCentroCusto = somatorioReceitaCentroCusto;
	}

	public BigDecimal getSomatorioReceita() {
		return somatorioReceita;
	}

	public void setSomatorioReceita(BigDecimal somatorioReceita) {
		this.somatorioReceita = somatorioReceita;
	}

	public BigDecimal getSomatorioReceitaEquipeMedica() {
		return somatorioReceitaEquipeMedica;
	}

	public void setSomatorioReceitaEquipeMedica(
			BigDecimal somatorioReceitaEquipeMedica) {
		this.somatorioReceitaEquipeMedica = somatorioReceitaEquipeMedica;
	}

	public BigDecimal getSomatorioReceitaEspecialidade() {
		return somatorioReceitaEspecialidade;
	}

	public void setSomatorioReceitaEspecialidade(
			BigDecimal somatorioReceitaEspecialidade) {
		this.somatorioReceitaEspecialidade = somatorioReceitaEspecialidade;
	}

	public String getTotalGeralCentroCustoStr() {
		totalGeralCentroCustoStr = valorFormatado(totalGeralCentroCusto);
		return totalGeralCentroCustoStr;
	}

	public void setTotalGeralCentroCustoStr(String totalGeralCentroCustoStr) {
		this.totalGeralCentroCustoStr = totalGeralCentroCustoStr;
	}

	public String getTotalGeralEquipeMedicaStr() {
		totalGeralEquipeMedicaStr = valorFormatado(totalGeralEquipeMedica);
		return totalGeralEquipeMedicaStr;
	}

	public void setTotalGeralEquipeMedicaStr(String totalGeralEquipeMedicaStr) {
		this.totalGeralEquipeMedicaStr = totalGeralEquipeMedicaStr;
	}

	public String getTotalGeralEspecialidadeStr() {
		totalGeralEspecialidadeStr = valorFormatado(totalGeralEspecialidade);
		return totalGeralEspecialidadeStr;
	}

	public void setTotalGeralEspecialidadeStr(String totalGeralEspecialidadeStr) {
		this.totalGeralEspecialidadeStr = totalGeralEspecialidadeStr;
	}

	public BigDecimal getTotalReceitaCentroCusto() {
		return totalReceitaCentroCusto;
	}

	public void setTotalReceitaCentroCusto(BigDecimal totalReceitaCentroCusto) {
		this.totalReceitaCentroCusto = totalReceitaCentroCusto;
	}

	public BigDecimal getTotalReceitaEspecialidade() {
		return totalReceitaEspecialidade;
	}

	public void setTotalReceitaEspecialidade(BigDecimal totalReceitaEspecialidade) {
		this.totalReceitaEspecialidade = totalReceitaEspecialidade;
	}

	public BigDecimal getTotalReceitaEquipeMedica() {
		return totalReceitaEquipeMedica;
	}

	public void setTotalReceitaEquipeMedica(BigDecimal totalReceitaEquipeMedica) {
		this.totalReceitaEquipeMedica = totalReceitaEquipeMedica;
	}

	public String getTotalReceitaCentroCustoStr() {
		totalReceitaCentroCustoStr = valorFormatado(totalReceitaCentroCusto);
		return totalReceitaCentroCustoStr;
	}

	public void setTotalReceitaCentroCustoStr(String totalReceitaCentroCustoStr) {
		this.totalReceitaCentroCustoStr = totalReceitaCentroCustoStr;
	}

	public String getTotalReceitaEquipeMedicaStr() {
		totalReceitaEquipeMedicaStr = valorFormatado(totalReceitaEquipeMedica);
		return totalReceitaEquipeMedicaStr;
	}

	public void setTotalReceitaEquipeMedicaStr(String totalReceitaEquipeMedicaStr) {
		this.totalReceitaEquipeMedicaStr = totalReceitaEquipeMedicaStr;
	}

	public String getTotalReceitaEspecialidadeStr() {
		totalReceitaEspecialidadeStr = valorFormatado(totalReceitaEspecialidade);
		return totalReceitaEspecialidadeStr;
	}

	public void setTotalReceitaEspecialidadeStr(String totalReceitaEspecialidadeStr) {
		this.totalReceitaEspecialidadeStr = totalReceitaEspecialidadeStr;
	}

	public List<DetalheConsumoVO> getListaDetalheConsumo() {
		return listaDetalheConsumo;
	}

	public void setListaDetalheConsumo(List<DetalheConsumoVO> listaDetalheConsumo) {
		this.listaDetalheConsumo = listaDetalheConsumo;
	}

	public CalculoAtendimentoPacienteVO getVo() {
		return vo;
	}

	public void setVo(CalculoAtendimentoPacienteVO vo) {
		this.vo = vo;
	}

	public Integer getCctCodigo() {
		return cctCodigo;
	}

	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}

	public List<SigCalculoAtdProcedimentos> getListaProcedimentosInternacao() {
		return listaProcedimentosInternacao;
	}

	public void setListaProcedimentosInternacao(
			List<SigCalculoAtdProcedimentos> listaProcedimentosInternacao) {
		this.listaProcedimentosInternacao = listaProcedimentosInternacao;
	}

}
