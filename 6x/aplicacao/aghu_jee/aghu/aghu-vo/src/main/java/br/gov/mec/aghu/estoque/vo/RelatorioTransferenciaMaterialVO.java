/**
 * 
 */
package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import br.gov.mec.aghu.model.ScoClassifMatNiv5;

/**
 * @author lessandro.lucas
 *
 */

public class RelatorioTransferenciaMaterialVO implements Serializable, Cloneable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -103407750630093673L;
	private Integer seq;
	private Boolean indTransferencia;
	private Date dtGeracao;
	private Date dtEfetivacao;
	private String dtEfetivacaoStr;
	private Date dtEstorno;
	private Short almSeq;
	private String almSeqDescricao;
	private Short almSeqRecebe;
	private String almSeqRecebeDescricao;
	private ScoClassifMatNiv5 cn5;
	private String cn5Descricao;
	private String cn4Descricao;
	private String cn3Descricao;
	private String cn2Descricao;
	private String cn1Descricao;
	private String gmtDescricao;
	private Integer cn5Codigo;
	private Integer cn4Codigo;
	private Integer cn3Codigo;
	private Integer cn2Codigo;
	private Integer cn1Codigo;
	private Integer gmtCodigo;
	private String nomeRequisitante;
	private Boolean indEfetivada;
	private Boolean indEstorno;
	private Integer diasEstqMinimo = 0;
	private Integer qtdEnviada;
	private Integer matCodigo; 
	private String nome;//
	private String endereco;
	private String enderecoOrigem;
	private String enderecoDestino;
	private Integer trnsfOrigem;
	private String umdCodigo;
	private String umdDescricao;
	private Integer qtdeEstqMin = 0;
	private Integer qtdeDisponivelOrigem = 0;
	private Integer qtdeDisponivelDestino = 0;
	private Integer qtdeBloqueada = 0;
	private Integer qtdeBloqEntrTransf = 0;
	private Boolean indImprime2Vias;
	private String nomeInstituicao;
	private Integer transferenciaDestino;
	private Integer ordemTela;	
	
	public ScoClassifMatNiv5 getCn5() {
		return cn5;
	}
	public void setCn5(ScoClassifMatNiv5 cn5) {
		this.cn5 = cn5;
	}
	public String getCn5Descricao() {
		return cn5Descricao;
	}
	public void setCn5Descricao(String cn5Descricao) {
		this.cn5Descricao = cn5Descricao;
	}
	public String getCn4Descricao() {
		return cn4Descricao;
	}
	public void setCn4Descricao(String cn4Descricao) {
		this.cn4Descricao = cn4Descricao;
	}
	public String getCn3Descricao() {
		return cn3Descricao;
	}
	public void setCn3Descricao(String cn3Descricao) {
		this.cn3Descricao = cn3Descricao;
	}
	public String getCn2Descricao() {
		return cn2Descricao;
	}
	public void setCn2Descricao(String cn2Descricao) {
		this.cn2Descricao = cn2Descricao;
	}
	public String getCn1Descricao() {
		return cn1Descricao;
	}
	public void setCn1Descricao(String cn1Descricao) {
		this.cn1Descricao = cn1Descricao;
	}
	public String getGmtDescricao() {
		return gmtDescricao;
	}
	public void setGmtDescricao(String gmtDescricao) {
		this.gmtDescricao = gmtDescricao;
	}
	public Integer getQtdeDisponivelDestino() {
		return qtdeDisponivelDestino;
	}
	public void setQtdeDisponivelDestino(Integer qtdeDisponivelDestino) {
		this.qtdeDisponivelDestino = qtdeDisponivelDestino;
	}
	public Integer getQtdeBloqueada() {
		return qtdeBloqueada;
	}
	public void setQtdeBloqueada(Integer qtdeBloqueada) {
		this.qtdeBloqueada = qtdeBloqueada;
	}
	public Boolean getIndImprime2Vias() {
		return indImprime2Vias;
	}
	public void setIndImprime2Vias(Boolean indImprime2Vias) {
		this.indImprime2Vias = indImprime2Vias;
	}
	public Integer getQtdeEstqMin() {
		return qtdeEstqMin;
	}
	public void setQtdeEstqMin(Integer qtdeEstqMin) {
		this.qtdeEstqMin = qtdeEstqMin;
	}
	public Integer getQtdeDisponivelOrigem() {
		return qtdeDisponivelOrigem;
	}
	public void setQtdeDisponivelOrigem(Integer qtdeDisponivelOrigem) {
		this.qtdeDisponivelOrigem = qtdeDisponivelOrigem;
	}
	public Integer getQtdBloqueada() {
		return qtdeBloqueada;
	}
	public void setQtdBloqueada(Integer qtdeBloqueada) {
		this.qtdeBloqueada = qtdeBloqueada;
	}
	public Integer getQtdeBloqEntrTransf() {
		return qtdeBloqEntrTransf;
	}
	public void setQtdeBloqEntrTransf(Integer qtdeBloqEntrTransf) {
		this.qtdeBloqEntrTransf = qtdeBloqEntrTransf;
	}
	public String getUmdCodigo() {
		return umdCodigo;
	}
	public void setUmdCodigo(String umdCodigo) {
		this.umdCodigo = umdCodigo;
	}
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public Boolean getIndTransferencia() {
		return indTransferencia;
	}
	public String getIndTransferenciaStr() {
		if(indTransferencia) {
			return "Automática";			
		} else {
			return "Eventual";			
		}
	}
	public void setIndTransferencia(Boolean indTransferencia) {
		this.indTransferencia = indTransferencia;
	}
	public Date getDtGeracao() {
		return dtGeracao;
	}
	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}
	public Date getDtEfetivacao() {
		return dtEfetivacao;
	}
	public void setDtEfetivacao(Date dtEfetivacao) {
		this.dtEfetivacao = dtEfetivacao;
	}
	public Date getDtEstorno() {
		return dtEstorno;
	}
	public void setDtEstorno(Date dtEstorno) {
		this.dtEstorno = dtEstorno;
	}
	public void setDtEstorno(Timestamp dtEstorno) {
		this.dtEstorno = dtEstorno;
	}
	public Short getAlmSeq() {
		return almSeq;
	}
	public void setAlmSeq(Short almSeq) {
		this.almSeq = almSeq;
	}
	public String getAlmSeqDescricao() {
		return almSeqDescricao;
	}
	public void setAlmSeqDescricao(String almSeqDescricao) {
		this.almSeqDescricao = almSeqDescricao;
	}
	public Short getAlmSeqRecebe() {
		return almSeqRecebe;
	}
	public void setAlmSeqRecebe(Short almSeqRecebe) {
		this.almSeqRecebe = almSeqRecebe;
	}
	public String getAlmSeqRecebeDescricao() {
		return almSeqRecebeDescricao;
	}
	public void setAlmSeqRecebeDescricao(String almSeqRecebeDescricao) {
		this.almSeqRecebeDescricao = almSeqRecebeDescricao;
	}
	
	public String getNomeRequisitante() {
		return nomeRequisitante;
	}
	public void setNomeRequisitante(String nomeRequisitante) {
		this.nomeRequisitante = nomeRequisitante;
	}
	public Integer getMatCodigo() {
		return matCodigo;
	}
	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getEnderecoOrigem() {
		return enderecoOrigem;
	}
	public void setEnderecoOrigem(String enderecoOrigem) {
		this.enderecoOrigem = enderecoOrigem;
	}
	
	public String getEnderecoDestino() {
		return enderecoDestino;
	}
	public String getEndereco() {
		return endereco;
	}
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}
	public void setEnderecoDestino(String enderecoDestino) {
		this.enderecoDestino = enderecoDestino;
	}
	public Integer getTrnsfOrigem() {
		return trnsfOrigem;
	}
	public void setTrnsfOrigem(Integer trnsfOrigem) {
		this.trnsfOrigem = trnsfOrigem;
	}
	public Boolean getIndEfetivada() {
		return indEfetivada;
	}
	public String getIndEfetivadaStr() {
		if(indEfetivada) {
			return "Efetivada em: ";			
		} else {
			return "Não efetivada";			
		}
	}
	public void setIndEfetivada(Boolean indEfetivada) {
		this.indEfetivada = indEfetivada;
	}
	public Boolean getIndEstorno() {
		return indEstorno;
	}
	public String getIndEstornoStr() {
		if(indEstorno) {
			return "Estornada em: ";			
		} else {
			return "";
		}
	}
	public void setIndEstorno(Boolean indEstorno) {
		this.indEstorno = indEstorno;
	}
	public Integer getDiasEstqMinimo() {
		return diasEstqMinimo;
	}
	public void setDiasEstqMinimo(Integer diasEstqMinimo) {
		this.diasEstqMinimo = diasEstqMinimo;
	}
	public Integer getQtdEnviada() {
		return qtdEnviada;
	}
	public void setQtdEnviada(Integer qtdEnviada) {
		this.qtdEnviada = qtdEnviada;
	}	
	
	public Integer getCn5Codigo() {
		return cn5Codigo;
	}
	public void setCn5Codigo(Integer cn5Codigo) {
		this.cn5Codigo = cn5Codigo;
	}
	public Integer getCn4Codigo() {
		return cn4Codigo;
	}
	public void setCn4Codigo(Integer cn4Codigo) {
		this.cn4Codigo = cn4Codigo;
	}
	public Integer getCn3Codigo() {
		return cn3Codigo;
	}
	public void setCn3Codigo(Integer cn3Codigo) {
		this.cn3Codigo = cn3Codigo;
	}
	public Integer getCn2Codigo() {
		return cn2Codigo;
	}
	public void setCn2Codigo(Integer cn2Codigo) {
		this.cn2Codigo = cn2Codigo;
	}
	public Integer getCn1Codigo() {
		return cn1Codigo;
	}
	public void setCn1Codigo(Integer cn1Codigo) {
		this.cn1Codigo = cn1Codigo;
	}
	public Integer getGmtCodigo() {
		return gmtCodigo;
	}
	public void setGmtCodigo(Integer gmtCodigo) {
		this.gmtCodigo = gmtCodigo;
	}
	public String getUmdDescricao() {
		return umdDescricao;
	}
	public void setUmdDescricao(String umdDescricao) {
		this.umdDescricao = umdDescricao;
	}
	public String getNomeInstituicao() {
		return nomeInstituicao;
	}
	public void setNomeInstituicao(String nomeInstituicao) {
		this.nomeInstituicao = nomeInstituicao;
	}
	public Integer getTransferenciaDestino() {
		return transferenciaDestino;
	}
	public void setTransferenciaDestino(Integer transferenciaDestino) {
		this.transferenciaDestino = transferenciaDestino;
	}
	public String getDtEfetivacaoStr() {
		return dtEfetivacaoStr;
	}
	public void setDtEfetivacaoStr(String dtEfetivacaoStr) {
		this.dtEfetivacaoStr = dtEfetivacaoStr;
	}
	public String getRetornaDescricao() {
		return getCn5Descricao();
	}

	public enum Fields{
		
		NUM_TRANFERENCIA_MATERIAL("seq"), 
		DT_GERACAO("dtGeracao"),
		DT_ESTORNO("dtEstorno"),
		DT_EFETIVACAO("dtEfetivacao"),
		ALMOX_ORIGEM("almSeq"), 
		ALMOX_DESTINO("almSeqRecebe"),
		ALMOX_ORIGEM_DESCRICAO("almSeqDescricao"), 
		ALMOX_DESTINO_DESCRICAO("almSeqRecebeDescricao"),
		CN5("cn5"),
		CN5_DESCRICAO("cn5Descricao"),
		CN4_DESCRICAO("cn4Descricao"),
		CN3_DESCRICAO("cn3Descricao"),
		CN2_DESCRICAO("cn2Descricao"),
		CN1_DESCRICAO("cn1Descricao"),
		GMT_DESCRICAO("gmtDescricao"),
		CN5_CODIGO("cn5Codigo"),
		CN4_CODIGO("cn4Codigo"),
		CN3_CODIGO("cn3Codigo"),
		CN2_CODIGO("cn2Codigo"),
		CN1_CODIGO("cn1Codigo"),
		GMT_CODIGO("gmtCodigo"),
		NOME("nome"), 
		MAT_CODIGO("matCodigo"), 
		NOME_REQUISITANTE("nomeRequisitante"),
		ENDERECO_ORIGEM("enderecoOrigem"),
		ENDERECO_DESTINO("enderecoDestino"),
		TRNSF_ORIGEM("trnsfOrigem"),
		IND_TRANF_AUTOMATICA("indTransferencia"),
		IND_EFETIVADA("indEfetivada"),
		IND_ESTORNO("indEstorno"),
		DIAS_ESTOQUE_MINIMO("diasEstqMinimo"),
		QTDE_ENVIADA("qtdEnviada"),
		UMD_CODIGO("umdCodigo"),
		UMD_DESCRICAO("umdDescricao"),
		NOME_INSTITUICAO("nomeInstituicao"),
		QTD_ESTOQ_MIN("qtdeEstqMin"),
		QTD_DISPONIVEL_ORIGEM("qtdeDisponivelOrigem"),
		QTD_DISPONIVEL_DESTINO("qtdeDisponivelDestino"),
		QTD_BLOQUEADA("qtdeBloqueada"),
		QTD_BLOQ_ENTR_TRANSF("qtdeBloqEntrTransf"),
		TRNSF_DEST("transferenciaDestino");
		
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	public RelatorioTransferenciaMaterialVO copiar(){
		try {
			return (RelatorioTransferenciaMaterialVO) this.clone();
		} catch (CloneNotSupportedException e) {
			// engolir exceção nunca vai acontecer pois o bojeto é clonnable.
			return null;
		}
	}
	
	public Integer getOrdemTela() {
		return ordemTela;
	}

	public void setOrdemTela(Integer ordemTela) {
		this.ordemTela = ordemTela;
	}
	
}