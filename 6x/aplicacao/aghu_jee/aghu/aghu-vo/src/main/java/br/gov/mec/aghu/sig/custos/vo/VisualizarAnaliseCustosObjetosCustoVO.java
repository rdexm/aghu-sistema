package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;
import java.math.RoundingMode;

import br.gov.mec.aghu.core.commons.BaseBean;

public class VisualizarAnaliseCustosObjetosCustoVO implements BaseBean{

	private static final long serialVersionUID = -3845213605608508206L;

	private Integer seqCentroProducao;
	private String nomeCentroProducao;
	private Integer codigoCentroCusto;
	private String nomeCentroCusto;

	private Integer seqCustoVersao;
	private Integer seqObjetoCusto;
	private String nomeObjetoCusto;

	private BigDecimal valorDireto;
	private BigDecimal valorIndireto;
	private BigDecimal qtAssistencial;
	private BigDecimal vlrObjetoCusto;
	private BigDecimal custoMedio;
	private Short seqPagador;
	private String nomePagador;
	
	private String tipoObjetoCusto;

	private BigDecimal pessoal;
	private BigDecimal insumos;
	private BigDecimal equipamentos;
	private BigDecimal servicos;
	private BigDecimal total;

	private static void create(VisualizarAnaliseCustosObjetosCustoVO vo, Object[] objects) {
		
		if (objects[0] != null) {
			vo.setSeqCentroProducao(Integer.parseInt(objects[0].toString()));
		}
		if (objects[1] != null) {
			vo.setNomeCentroProducao(objects[1].toString());
		}
		if (objects[2] != null) {
			vo.setCodigoCentroCusto(Integer.parseInt(objects[2].toString()));
		}
		if (objects[3] != null) {
			vo.setNomeCentroCusto(objects[3].toString());
		}
	}

	public static VisualizarAnaliseCustosObjetosCustoVO createObjetoCusto(Object[] objects) {
		VisualizarAnaliseCustosObjetosCustoVO vo = new VisualizarAnaliseCustosObjetosCustoVO();
		VisualizarAnaliseCustosObjetosCustoVO.create(vo, objects);

		if (objects[4] != null) {
			vo.setSeqCustoVersao(Integer.parseInt(objects[4].toString()));
		}
		if (objects[5] != null) {
			vo.setSeqObjetoCusto(Integer.parseInt(objects[5].toString()));
		}
		if (objects[6] != null) {
			vo.setNomeObjetoCusto(objects[6].toString());
		}
		if (objects[7] != null) {
			vo.setTipoObjetoCusto(objects[7].toString());
		}
		if (objects[8] != null) {
			vo.setValorDireto(new BigDecimal(objects[8].toString()));
		}
		if (objects[9] != null) {
			vo.setValorIndireto(new BigDecimal(objects[9].toString()));
		}
		if (objects[10] != null) {
			vo.setQtAssistencial(new BigDecimal(objects[10].toString()));
		}
		if (objects[11] != null) {
			vo.setVlrObjetoCusto(new BigDecimal(objects[11].toString()));
		}
		
		VisualizarAnaliseCustosObjetosCustoVO.adicionarInformacoesFontePagadora(vo, objects);
		
		if(vo.getTipoObjetoCusto().equals("AS")){
			vo.setCustoMedio(vo.getVlrObjetoCusto().divide(vo.getQtAssistencial(), RoundingMode.HALF_UP));
		}

		vo.setTotal(vo.getValorIndireto().add(vo.getValorDireto()));
		return vo;
	}
	
	private static void adicionarInformacoesFontePagadora(VisualizarAnaliseCustosObjetosCustoVO vo, Object[] objects){
		if (objects[12] != null) {
			vo.setSeqPagador(Short.parseShort(objects[12].toString()));
		}
		if (objects[13] != null) {
			vo.setNomePagador(objects[13].toString());
		}
	}

	public static VisualizarAnaliseCustosObjetosCustoVO createCentroCusto(Object[] objects) {
		VisualizarAnaliseCustosObjetosCustoVO vo = new VisualizarAnaliseCustosObjetosCustoVO();
		VisualizarAnaliseCustosObjetosCustoVO.create(vo, objects);

		vo.setSeqCustoVersao(0);

		if (objects[4] != null) {
			vo.setInsumos(new BigDecimal(objects[4].toString()));
		}
		if (objects[5] != null) {
			vo.setPessoal(new BigDecimal(objects[5].toString()));
		}
		if (objects[6] != null) {
			vo.setEquipamentos(new BigDecimal(objects[6].toString()));
		}
		if (objects[7] != null) {
			vo.setServicos(new BigDecimal(objects[7].toString()));
		}
		if (objects[8] != null && objects[9] != null && objects[10] != null && objects[11] != null) {
			vo.setValorIndireto( new BigDecimal(objects[8].toString()).add(new BigDecimal(objects[9].toString())).add(new BigDecimal(objects[10].toString())).add(new BigDecimal(objects[11].toString())));
		}
		if (objects[12] != null) {
			vo.setTotal(new BigDecimal(objects[12].toString()));
		}
		vo.setValorDireto(vo.getInsumos().add(vo.getPessoal()).add(vo.getEquipamentos()).add(vo.getServicos()));
		return vo;
	}

	public String getNomeCentroProducao() {
		return nomeCentroProducao;
	}

	public void setNomeCentroProducao(String nomeCentroProducao) {
		this.nomeCentroProducao = nomeCentroProducao;
	}

	public BigDecimal getValorIndireto() {
		return valorIndireto;
	}

	public void setValorIndireto(BigDecimal valorIndireto) {
		this.valorIndireto = valorIndireto;
	}

	public BigDecimal getValorDireto() {
		return valorDireto;
	}

	public void setValorDireto(BigDecimal valorDireto) {
		this.valorDireto = valorDireto;
	}

	public BigDecimal getQtAssistencial() {
		return qtAssistencial;
	}

	public void setQtAssistencial(BigDecimal qtAssistencial) {
		this.qtAssistencial = qtAssistencial;
	}

	public String getNomeObjetoCusto() {
		return nomeObjetoCusto;
	}

	public void setNomeObjetoCusto(String nomeObjetoCusto) {
		this.nomeObjetoCusto = nomeObjetoCusto;
	}

	public Integer getSeqObjetoCusto() {
		return seqObjetoCusto;
	}

	public void setSeqObjetoCusto(Integer seqObjetoCusto) {
		this.seqObjetoCusto = seqObjetoCusto;
	}

	public Integer getSeqCustoVersao() {
		return seqCustoVersao;
	}

	public void setSeqCustoVersao(Integer seqCustoVersao) {
		this.seqCustoVersao = seqCustoVersao;
	}

	public String getNomeCentroCusto() {
		return nomeCentroCusto;
	}

	public void setNomeCentroCusto(String nomeCentroCusto) {
		this.nomeCentroCusto = nomeCentroCusto;
	}

	public BigDecimal getServicos() {
		return servicos;
	}

	public void setServicos(BigDecimal servicos) {
		this.servicos = servicos;
	}

	public BigDecimal getEquipamentos() {
		return equipamentos;
	}

	public void setEquipamentos(BigDecimal equipamentos) {
		this.equipamentos = equipamentos;
	}

	public BigDecimal getInsumos() {
		return insumos;
	}

	public void setInsumos(BigDecimal insumos) {
		this.insumos = insumos;
	}

	public BigDecimal getPessoal() {
		return pessoal;
	}

	public void setPessoal(BigDecimal pessoal) {
		this.pessoal = pessoal;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public Integer getCodigoCentroCusto() {
		return codigoCentroCusto;
	}

	public void setCodigoCentroCusto(Integer codigoCentroCusto) {
		this.codigoCentroCusto = codigoCentroCusto;
	}

	public BigDecimal getVlrObjetoCusto() {
		return vlrObjetoCusto;
	}

	public void setVlrObjetoCusto(BigDecimal vlrObjetoCusto) {
		this.vlrObjetoCusto = vlrObjetoCusto;
	}

	public String getTipoObjetoCusto() {
		return tipoObjetoCusto;
	}

	public void setTipoObjetoCusto(String tipoObjetoCusto) {
		this.tipoObjetoCusto = tipoObjetoCusto;
	}

	public BigDecimal getCustoMedio() {
		return custoMedio;
	}

	public void setCustoMedio(BigDecimal custoMedio) {
		this.custoMedio = custoMedio;
	}

	public String getNomePagador() {
		return nomePagador;
	}

	public void setNomePagador(String nomePagador) {
		this.nomePagador = nomePagador;
	}

	public Short getSeqPagador() {
		return seqPagador;
	}

	public void setSeqPagador(Short seqPagador) {
		this.seqPagador = seqPagador;
	}

	public Integer getSeqCentroProducao() {
		return seqCentroProducao;
	}

	public void setSeqCentroProducao(Integer seqCentroProducao) {
		this.seqCentroProducao = seqCentroProducao;
	}
}