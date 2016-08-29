package br.gov.mec.aghu.exames.solicitacao.vo;

import java.io.Serializable;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;

public abstract class MensagemSolicitacaoExameGrupoVO implements Serializable{

	private static final long serialVersionUID = 3487495895866393L;
	
	public enum TipoFilaExameInternet {
		NOVO,REENVIO;
	}
	
	private Integer seqSolicitacaoExame;
	private Integer seqExameInternetGrupo;
	private AelItemSolicitacaoExames itemSolicitacaoExames;

	public Integer getSeqSolicitacaoExame() {
		return seqSolicitacaoExame;
	}

	public void setSeqSolicitacaoExame(Integer seqSolicitacaoExame) {
		this.seqSolicitacaoExame = seqSolicitacaoExame;
	}

	public Integer getSeqExameInternetGrupo() {
		return seqExameInternetGrupo;
	}

	public void setSeqExameInternetGrupo(Integer seqExameInternetGrupo) {
		this.seqExameInternetGrupo = seqExameInternetGrupo;
	}

	public AelItemSolicitacaoExames getItemSolicitacaoExames() {
		return itemSolicitacaoExames;
	}

	public void setItemSolicitacaoExames(AelItemSolicitacaoExames itemSolicitacaoExames) {
		this.itemSolicitacaoExames = itemSolicitacaoExames;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (o instanceof MensagemSolicitacaoExameGrupoVO){
			if (((MensagemSolicitacaoExameGrupoVO)o).seqExameInternetGrupo != null && ((MensagemSolicitacaoExameGrupoVO)o).seqSolicitacaoExame != null 
					&& this.seqExameInternetGrupo != null && this.seqSolicitacaoExame != null){
				if (((MensagemSolicitacaoExameGrupoVO)o).seqExameInternetGrupo.intValue() == this.seqExameInternetGrupo.intValue()
						&& ((MensagemSolicitacaoExameGrupoVO)o).seqSolicitacaoExame.intValue() == this.seqSolicitacaoExame.intValue()){
					return true;
				}
			}
		}
		return false;
	}
	
	public abstract TipoFilaExameInternet getTipoFila();
}
