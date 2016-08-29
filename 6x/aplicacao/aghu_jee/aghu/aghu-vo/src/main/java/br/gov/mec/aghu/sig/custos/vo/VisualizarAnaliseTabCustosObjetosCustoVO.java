package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.core.commons.BaseBean;

public class VisualizarAnaliseTabCustosObjetosCustoVO implements BaseBean, Comparable<VisualizarAnaliseTabCustosObjetosCustoVO>, CalculoObjetosCentrosCustosInterface {

	private static final String BARRA_AF = " / AF ";

	private static final long serialVersionUID = -3845213605608508206L;

	private String descricao;
	private String nome;
	private Integer ocvSeq;
	private Double quantidade = 0D;
	private Double quantidadeRealizada = 0D;
	private Double totalAlocado = 0D;
	private Double totalRateado = 0D;
	private Double naoRateado = 0D;
	private Double naoAlocado = 0D;
	private BigDecimal total = BigDecimal.ZERO;
	private Double percentual;
	private boolean calculaTotal;
	private String unidadeMedida;
	private String codigo;
	private Long nrContrato;
	private Integer lctNumero;
	private Short nroComplemento;
	private Double totalInsumos = 0D;
	private Double totalPessoas = 0D;
	private Double totalEquipamentos = 0D;
	private Double totalServicos = 0D;
	private String nomeFornecedor;
	private String nomeServico;
	private Integer iteracao;

	@Override
	public boolean equals(Object obj) {
		return this.descricao.equals((String) obj);
	}

	public static VisualizarAnaliseTabCustosObjetosCustoVO create(Object[] objects, boolean calculaTotal) {
		VisualizarAnaliseTabCustosObjetosCustoVO vo = new VisualizarAnaliseTabCustosObjetosCustoVO();
		vo.setCalculaTotal(calculaTotal);

		if (objects[0] != null) {
			vo.setDescricao(objects[0].toString());
		}
		if (objects[1] != null) {
			vo.setQuantidade(Double.parseDouble(objects[1].toString()));
		}
		if (objects[2] != null) {
			vo.setQuantidadeRealizada(Double.parseDouble(objects[2].toString()));
		}
		if (objects[3] != null) {
			vo.setTotalAlocado(Double.parseDouble(objects[3].toString()));
		}
		if (objects[4] != null) {
			vo.setTotalRateado(Double.parseDouble(objects[4].toString()));
		}
		if (!calculaTotal && objects[5] != null) {
			vo.setTotal(new BigDecimal(objects[5].toString()));
		}
		return vo;
	}

	public static VisualizarAnaliseTabCustosObjetosCustoVO createMovimentoPessoa(Object[] objects, boolean calculaTotal) {
		VisualizarAnaliseTabCustosObjetosCustoVO vo = new VisualizarAnaliseTabCustosObjetosCustoVO();
		vo.setCalculaTotal(calculaTotal);

		if (objects[0] != null) {
			vo.setDescricao(objects[0].toString());
		}
		if (objects[1] != null) {
			vo.setQuantidade(Double.parseDouble(objects[1].toString()));
		}
		if (objects[2] != null) {
			vo.setQuantidadeRealizada(Double.parseDouble(objects[2].toString()));
		}
		if (objects[3] != null) {
			vo.setTotalAlocado(Double.parseDouble(objects[3].toString()));
		}
		if (objects[4] != null) {
			vo.setTotalRateado(Double.parseDouble(objects[4].toString()));
		}
		if (objects[5] != null) {
			vo.setNaoAlocado(Double.parseDouble(objects[5].toString()));
		}
		if (!calculaTotal && objects[6] != null) {
			vo.setTotal(new BigDecimal(objects[6].toString()));
		}
		return vo;
	}

	public static VisualizarAnaliseTabCustosObjetosCustoVO createMovimentoInsumo(Object[] objects) {

		VisualizarAnaliseTabCustosObjetosCustoVO vo = new VisualizarAnaliseTabCustosObjetosCustoVO();
		vo.setCalculaTotal(true);

		if (objects[0] != null) {
			vo.setCodigo(Integer.parseInt(objects[0].toString()));
		}

		if (objects[1] != null) {
			vo.setDescricao(objects[1].toString());
		}

		if (objects[3] != null) {
			vo.setQuantidadeRealizada(Double.parseDouble(objects[3].toString()));

			if (objects[2] != null) {
				vo.setUnidadeMedida(objects[2].toString());
			}
		}

		if (objects[4] != null) {
			vo.setTotalAlocado(Double.parseDouble(objects[4].toString()));
		}

		if (objects[5] != null) {
			vo.setTotalRateado(Double.parseDouble(objects[5].toString()));
		}

		return vo;
	}

	public static VisualizarAnaliseTabCustosObjetosCustoVO createMovimentoInsumoCC(Object[] objects) {

		VisualizarAnaliseTabCustosObjetosCustoVO vo = new VisualizarAnaliseTabCustosObjetosCustoVO();
		vo.setCalculaTotal(false);
		
		if (objects[0] != null) {
			vo.setCodigo(Integer.parseInt(objects[0].toString()));
		}

		if (objects[1] != null) {
			vo.setDescricao(objects[1].toString());
		}

		if (objects[3] != null) {
			vo.setQuantidadeRealizada(Double.parseDouble(objects[3].toString()));

			if (objects[2] != null) {
				vo.setUnidadeMedida(objects[2].toString());
			}
		}

		if (objects[4] != null) {
			vo.setTotalAlocado(Double.parseDouble(objects[4].toString()));
		}

		if (objects[5] != null) {
			vo.setTotalRateado(Double.parseDouble(objects[5].toString()));
		}

		if (objects[6] != null) {
			//vo.setNaoRateado(Double.parseDouble(objects[6].toString()));
			vo.setNaoAlocado(Double.parseDouble(objects[6].toString()));
		}

		if (objects[7] != null) {
			vo.setTotal(new BigDecimal(objects[7].toString()));
		}

		return vo;
	}

	public static VisualizarAnaliseTabCustosObjetosCustoVO createMovimentoEquipamentoCC(Object[] objects) {

		VisualizarAnaliseTabCustosObjetosCustoVO vo = new VisualizarAnaliseTabCustosObjetosCustoVO();
		vo.setCalculaTotal(false);

		if (objects[0] != null) {
			vo.setDescricao(objects[0].toString());
		}

		if (objects[1] != null) {
			vo.setQuantidade(Double.parseDouble(objects[1].toString()));
		}

		if (objects[2] != null) {
			vo.setQuantidadeRealizada(Double.parseDouble(objects[2].toString()));
		}

		if (objects[3] != null) {
			vo.setTotalAlocado(Double.parseDouble(objects[3].toString()));
		}

		if (objects[4] != null) {
			vo.setTotalRateado(Double.parseDouble(objects[4].toString()));
		}

		if (objects[5] != null) {
			vo.setNaoAlocado(Double.parseDouble(objects[5].toString()));
		}

		if (objects[6] != null) {
			vo.setTotal(new BigDecimal(objects[6].toString()));
		}

		return vo;
	}

	public static VisualizarAnaliseTabCustosObjetosCustoVO createMovimentoIndireto(Object[] objects, boolean calculaTotal) {

		VisualizarAnaliseTabCustosObjetosCustoVO vo = new VisualizarAnaliseTabCustosObjetosCustoVO();
		vo.setCalculaTotal(calculaTotal);

		if (objects[0] != null) {
			vo.setCodigo(objects[0].toString());
		}
		
		if (objects[1] != null) {
			vo.setDescricao(objects[1].toString());
		}

		if (objects[2] != null) {
			vo.setNome(objects[2].toString());
		}

		if (objects[3] != null) {
			vo.setOcvSeq(Integer.parseInt(objects[3].toString()));
		}

		if (objects[4] != null) {
			vo.setTotalInsumos(Double.parseDouble(objects[4].toString()));
		}

		if (objects[5] != null) {
			vo.setTotalPessoas(Double.parseDouble(objects[5].toString()));
		}

		if (objects[6] != null) {
			vo.setTotalEquipamentos(Double.parseDouble(objects[6].toString()));
		}

		if (objects[7] != null) {
			vo.setTotalServicos(Double.parseDouble(objects[7].toString()));
		}

		return vo;
	}
	
	public static VisualizarAnaliseTabCustosObjetosCustoVO createMovimentoIndiretoIteracao(Object[] objects, boolean calculaTotal) {

		VisualizarAnaliseTabCustosObjetosCustoVO vo = new VisualizarAnaliseTabCustosObjetosCustoVO();
		vo.setCalculaTotal(calculaTotal);

		if (objects[0] != null) {
			vo.setCodigo(objects[0].toString());
		}
		
		if (objects[1] != null) {
			vo.setDescricao(objects[1].toString());
		}

		if (objects[2] != null) {
			vo.setNome(objects[2].toString());
		}

		if (objects[3] != null) {
			vo.setOcvSeq(Integer.parseInt(objects[3].toString()));
		}

		if (objects[4] != null) {
			vo.setIteracao(Integer.parseInt(objects[4].toString()));
		}
		
		if (objects[5] != null) {
			vo.setTotalInsumos(Double.parseDouble(objects[5].toString()));
		}

		if (objects[6] != null) {
			vo.setTotalPessoas(Double.parseDouble(objects[6].toString()));
		}

		if (objects[7] != null) {
			vo.setTotalEquipamentos(Double.parseDouble(objects[7].toString()));
		}

		if (objects[8] != null) {
			vo.setTotalServicos(Double.parseDouble(objects[8].toString()));
		}

		return vo;
	}
	
	public static VisualizarAnaliseTabCustosObjetosCustoVO createMovimentoIndiretoCC(Object[] objects, boolean calculaTotal) {

		VisualizarAnaliseTabCustosObjetosCustoVO vo = new VisualizarAnaliseTabCustosObjetosCustoVO();
		vo.setCalculaTotal(calculaTotal);

		if (objects[0] != null) {
			vo.setDescricao(objects[0].toString());
		}

		if (objects[1] != null) {
			vo.setNome(objects[1].toString());
		}
		
		if (objects[2] != null) {
			vo.setOcvSeq(Integer.parseInt(objects[2].toString()));
		}

		if (objects[3] != null) {
			vo.setTotalInsumos(Double.parseDouble(objects[3].toString()));
		}

		if (objects[4] != null) {
			vo.setTotalPessoas(Double.parseDouble(objects[4].toString()));
		}

		if (objects[5] != null) {
			vo.setTotalEquipamentos(Double.parseDouble(objects[5].toString()));
		}

		if (objects[6] != null) {
			vo.setTotalServicos(Double.parseDouble(objects[6].toString()));
		}

		if (objects[7] != null) {
			vo.setTotal(new BigDecimal(objects[7].toString()));
		}
		
		return vo;
	}
	
	public static VisualizarAnaliseTabCustosObjetosCustoVO createMovimentoIndiretoCCIteracao(Object[] objects, boolean calculaTotal) {

		VisualizarAnaliseTabCustosObjetosCustoVO vo = new VisualizarAnaliseTabCustosObjetosCustoVO();
		vo.setCalculaTotal(calculaTotal);

		if (objects[0] != null) {
			vo.setDescricao(objects[0].toString());
		}

		if (objects[1] != null) {
			vo.setNome(objects[1].toString());
		}
		
		if (objects[2] != null) {
			vo.setOcvSeq(Integer.parseInt(objects[2].toString()));
		}
		
		if (objects[3] != null) {
			vo.setIteracao(Integer.parseInt(objects[3].toString()));
		}

		if (objects[4] != null) {
			vo.setTotalInsumos(Double.parseDouble(objects[4].toString()));
		}

		if (objects[5] != null) {
			vo.setTotalPessoas(Double.parseDouble(objects[5].toString()));
		}

		if (objects[6] != null) {
			vo.setTotalEquipamentos(Double.parseDouble(objects[6].toString()));
		}

		if (objects[7] != null) {
			vo.setTotalServicos(Double.parseDouble(objects[7].toString()));
		}

		if (objects[8] != null) {
			vo.setTotal(new BigDecimal(objects[8].toString()));
		}
		
		return vo;
	}

	public static VisualizarAnaliseTabCustosObjetosCustoVO createMovimentoServico(Object[] objects, boolean calculaTotal, Integer tipoServico) {

		
		VisualizarAnaliseTabCustosObjetosCustoVO vo = new VisualizarAnaliseTabCustosObjetosCustoVO();
		vo.setCalculaTotal(calculaTotal);

		if (objects[0] != null && StringUtils.isNotBlank(objects[0].toString())) {
			vo.setNrContrato(Long.parseLong(objects[0].toString()));
		}

		if (objects[1] != null && StringUtils.isNotBlank(objects[1].toString())) {
			vo.setLctNumero(Integer.parseInt(objects[1].toString()));
		}

		if (objects[2] != null && StringUtils.isNotBlank(objects[2].toString())) {
			vo.setNroComplemento(Short.parseShort(objects[2].toString()));
		}
		
		if (objects[3] != null) {
			vo.setNomeFornecedor(objects[3].toString());
		}
		
		if (objects[4] != null) {
			vo.setNomeServico(objects[4].toString());
		}

		if (objects[5] != null) {
			vo.setQuantidadeRealizada(Double.parseDouble(objects[5].toString()));
		}

		createMovimentoServicoPt2(objects, calculaTotal, tipoServico, vo);
		

		return vo;
	}

	private static void createMovimentoServicoPt2(Object[] objects, boolean calculaTotal, Integer tipoServico, VisualizarAnaliseTabCustosObjetosCustoVO vo) {
		if (objects[6] != null) {
			vo.setTotalAlocado(Double.parseDouble(objects[6].toString()));
		}

		if (objects[7] != null) {
			vo.setTotalRateado(Double.parseDouble(objects[7].toString()));
		}

		if (!calculaTotal && objects[8] != null) {
			vo.setTotal(new BigDecimal(objects[8].toString()));
		}
		
		final Integer AUTOMATICO = 0;
		final Integer MANUAL = 1;
		final Integer AF = 2;

		if(tipoServico == AUTOMATICO ){
			vo.setDescricao(vo.getNrContrato() + BARRA_AF +vo.getLctNumero() + " - " + vo.getNroComplemento()); 
		}
		if(tipoServico ==  MANUAL){
			vo.setDescricao(vo.getNrContrato() + BARRA_AF +vo.getLctNumero());
		}
		
		if(tipoServico == AF){
			vo.setDescricao("AF " + vo.getLctNumero() + " / " + vo.getNroComplemento());	
		}
	}

	public static VisualizarAnaliseTabCustosObjetosCustoVO createMovimentoServicoCC(Object[] objects, boolean calculaTotal, Integer tipoServico) {

		VisualizarAnaliseTabCustosObjetosCustoVO vo = new VisualizarAnaliseTabCustosObjetosCustoVO();
		vo.setCalculaTotal(calculaTotal);

		if (objects[0] != null && StringUtils.isNotBlank(objects[0].toString())) {
			vo.setNrContrato(Long.parseLong(objects[0].toString()));
		}

		if (objects[1] != null && StringUtils.isNotBlank(objects[1].toString())) {
			vo.setLctNumero(Integer.parseInt(objects[1].toString()));
		}

		if (objects[2] != null && StringUtils.isNotBlank(objects[2].toString())) {
			vo.setNroComplemento(Short.parseShort(objects[2].toString()));
		}
		
		if (objects[3] != null) {
			vo.setNomeFornecedor(objects[3].toString());
		}
		
		if (objects[4] != null) {
			vo.setNomeServico(objects[4].toString());
		}

		createMovimentoServicoCCPt2(objects, calculaTotal, tipoServico, vo);


		return vo;
	}

	private static void createMovimentoServicoCCPt2(Object[] objects, boolean calculaTotal, Integer tipoServico, VisualizarAnaliseTabCustosObjetosCustoVO vo) {
		if (objects[5] != null) {
			vo.setQuantidadeRealizada(Double.parseDouble(objects[5].toString()));
		}

		if (objects[6] != null) {
			vo.setTotalAlocado(Double.parseDouble(objects[6].toString()));
		}

		if (objects[7] != null) {
			vo.setTotalRateado(Double.parseDouble(objects[7].toString()));
		}

		if (objects[8] != null) {
			vo.setNaoAlocado(Double.parseDouble(objects[8].toString()));
		}

		if (!calculaTotal && objects[9] != null) {
			vo.setTotal(new BigDecimal(objects[9].toString()));
		}
		
		final Integer AUTOMATICO = 0;
		final Integer MANUAL = 1;
		final Integer AF = 2;

		
		if(tipoServico == AUTOMATICO ){
			vo.setDescricao(vo.getNrContrato() + BARRA_AF +vo.getLctNumero() + " - " + vo.getNroComplemento()); 
		}
		if(tipoServico ==  MANUAL){
			vo.setDescricao(vo.getNrContrato() + BARRA_AF +vo.getLctNumero());
		}
		
		if(tipoServico == AF){
			vo.setDescricao("AF " + vo.getLctNumero() + " / " + vo.getNroComplemento());	
		}
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Double getTotalRateado() {
		return totalRateado;
	}

	public void setTotalRateado(Double totalRateado) {
		this.totalRateado = totalRateado != null ? Math.abs(totalRateado) : null;
		if (this.calculaTotal) {
			this.setTotal(new BigDecimal(getTotalAlocado() + getTotalRateado()));
		}
	}

	public Double getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Double quantidade) {
		this.quantidade = quantidade;
	}

	public Double getTotalAlocado() {
		return totalAlocado;
	}

	public void setTotalAlocado(Double totalAlocado) {
		this.totalAlocado = totalAlocado != null ? Math.abs(totalAlocado) : null;
		if (this.calculaTotal) {
			this.setTotal(new BigDecimal(getTotalAlocado() + getTotalRateado()));
		}
	}

	@Override
	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total != null ? total.abs() : null;
	}

	public Double getQuantidadeRealizada() {
		return quantidadeRealizada;
	}

	public void setQuantidadeRealizada(Double quantidadeRealizada) {
		this.quantidadeRealizada = quantidadeRealizada != null ? Math.abs(quantidadeRealizada) : null;
	}

	@Override
	public int compareTo(VisualizarAnaliseTabCustosObjetosCustoVO o) {
		if(o.getIteracao() != null){
			return this.getIteracao().compareTo(o.getIteracao());
		}
		else{
			return (this.getDescricao() + this.getNome()).compareToIgnoreCase(o.getDescricao() + o.getNome());
		}
	}

	public Double getPercentual() {
		return percentual;
	}

	@Override
	public void setPercentual(Double percentual) {
		this.percentual = percentual != null ? Math.abs(percentual) : null;
	}

	public boolean isCalculaTotal() {
		return calculaTotal;
	}

	public void setCalculaTotal(boolean calculaTotal) {
		this.calculaTotal = calculaTotal;
	}

	public String getUnidadeMedida() {
		return unidadeMedida;
	}

	public void setUnidadeMedida(String unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	/**
	 * Método que permite informar o código como um valor inteiro 
	 * @author rogeriovieira
	 * @param codigo valor inteiro que será convertido para string
	 */
	public void setCodigo(Integer codigo) {
		this.codigo = codigo.toString();
	}

	public Integer getLctNumero() {
		return lctNumero;
	}

	public void setLctNumero(Integer lctNumero) {
		this.lctNumero = lctNumero;
	}

	public Short getNroComplemento() {
		return nroComplemento;
	}

	public void setNroComplemento(Short nroComplemento) {
		this.nroComplemento = nroComplemento;
	}

	public Long getNrContrato() {
		return nrContrato;
	}

	public void setNrContrato(Long nrContrato) {
		this.nrContrato = nrContrato;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Double getTotalInsumos() {
		return totalInsumos;
	}

	public void setTotalInsumos(Double totalInsumos) {
		this.totalInsumos = totalInsumos != null ? Math.abs(totalInsumos) : null;
	}

	public Double getTotalPessoas() {
		return totalPessoas;
	}

	public void setTotalPessoas(Double totalPessoas) {
		this.totalPessoas = totalPessoas != null ? Math.abs(totalPessoas) : null;
	}

	public Double getTotalEquipamentos() {
		return totalEquipamentos;
	}

	public void setTotalEquipamentos(Double totalEquipamentos) {
		this.totalEquipamentos = totalEquipamentos != null ? Math.abs(totalEquipamentos) : null;
	}

	public Double getTotalServicos() {
		return totalServicos;
	}

	public void setTotalServicos(Double totalServicos) {
		this.totalServicos = totalServicos;
	}

	public Double getNaoRateado() {
		return naoRateado;
	}

	public void setNaoRateado(Double naoRateado) {
		this.naoRateado = naoRateado != null ? Math.abs(naoRateado) : null;
	}

	public Double getNaoAlocado() {
		return naoAlocado;
	}

	public void setNaoAlocado(Double naoAlocado) {
		this.naoAlocado = naoAlocado != null ? Math.abs(naoAlocado) : null;
	}

	public String getNomeServico() {
		return nomeServico;
	}

	public void setNomeServico(String nomeServico) {
		this.nomeServico = nomeServico;
	}

	public String getNomeFornecedor() {
		return nomeFornecedor;
	}

	public void setNomeFornecedor(String nomeFornecedor) {
		this.nomeFornecedor = nomeFornecedor;
	}
	
	public String getCodigoDescricaoFormatado() {
		if(codigo == null || codigo.isEmpty()) {
			return descricao;
		} else {
			return codigo.concat(" - ").concat(descricao);
		}
	}

	public Integer getIteracao() {
		return iteracao;
	}

	public void setIteracao(Integer iteracao) {
		this.iteracao = iteracao;
	}

	public Integer getOcvSeq() {
		return ocvSeq;
	}

	public void setOcvSeq(Integer ocvSeq) {
		this.ocvSeq = ocvSeq;
	}

}
