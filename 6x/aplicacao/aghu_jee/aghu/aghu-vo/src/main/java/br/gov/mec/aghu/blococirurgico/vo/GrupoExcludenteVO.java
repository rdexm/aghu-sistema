package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioTabelaGrupoExcludente;

public class GrupoExcludenteVO implements Serializable {
	private static final long serialVersionUID = 8722279933007190604L;
	private Integer codigo;
	private DominioTabelaGrupoExcludente cor;
	private List<ItemProcedimentoVO> listaGrupoExcludente;
	private Short iphPhoSeq;
	private Integer iphSeq;

	public Integer getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public DominioTabelaGrupoExcludente getCor() {
		return this.cor;
	}

	public void setCor(DominioTabelaGrupoExcludente cor) {
		this.cor = cor;
	}

	public List<ItemProcedimentoVO> getListaGrupoExcludente() {
		return this.listaGrupoExcludente;
	}

	public void setListaGrupoExcludente(List<ItemProcedimentoVO> listaGrupoExcludente) {
		this.listaGrupoExcludente = listaGrupoExcludente;
	}

	public Short getIphPhoSeq() {
		return iphPhoSeq;
	}

	public void setIphPhoSeq(Short iphPhoSeq) {
		this.iphPhoSeq = iphPhoSeq;
	}

	public Integer getIphSeq() {
		return iphSeq;
	}

	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}
	
	
}