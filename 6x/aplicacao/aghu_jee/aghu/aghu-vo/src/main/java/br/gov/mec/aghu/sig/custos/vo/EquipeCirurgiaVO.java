package br.gov.mec.aghu.sig.custos.vo;

import java.util.List;

import org.hibernate.transform.ResultTransformer;

public class EquipeCirurgiaVO implements ResultTransformer {

	private static final long serialVersionUID = 1264687248845290362L;

	private Integer seqGrupoOcupacao;
	private String ocaCarCodigo;
	private Integer ocaCodigo;
	private Integer cctCodigo;
	private String tipo;
	private Long count;

	public EquipeCirurgiaVO() {
	}

	public Integer getSeqGrupoOcupacao() {
		return seqGrupoOcupacao;
	}

	public void setSeqGrupoOcupacao(Integer seqGrupoOcupacao) {
		this.seqGrupoOcupacao = seqGrupoOcupacao;
	}

	public String getOcaCarCodigo() {
		return ocaCarCodigo;
	}

	public void setOcaCarCodigo(String ocaCarCodigo) {
		this.ocaCarCodigo = ocaCarCodigo;
	}

	public Integer getOcaCodigo() {
		return ocaCodigo;
	}

	public void setOcaCodigo(Integer ocaCodigo) {
		this.ocaCodigo = ocaCodigo;
	}

	public Integer getCctCodigo() {
		return cctCodigo;
	}

	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	@Override
	public Object transformTuple(Object[] tuple, String[] aliases) {

		EquipeCirurgiaVO vo = new EquipeCirurgiaVO();
		if (tuple[0] != null) {
			vo.setSeqGrupoOcupacao((Integer) tuple[0]);
		}
		if (tuple[1] != null) {
			vo.setOcaCarCodigo((String) tuple[1]);
		}
		if (tuple[2] != null) {
			vo.setOcaCodigo((Integer) tuple[2]);
		}
		if (tuple[3] != null) {
			vo.setCctCodigo((Integer) tuple[3]);
		}
		if (tuple[4] != null) {
			vo.setTipo((String) tuple[4]);
		}
		if (tuple[4] != null) {
			vo.setCount((Long) tuple[5]);
		}
		return vo;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List transformList(List collection) {
		return collection;
	}

}
