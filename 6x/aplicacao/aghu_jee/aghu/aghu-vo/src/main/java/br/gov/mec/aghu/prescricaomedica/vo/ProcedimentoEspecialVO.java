package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioTipoProcedimentoEspecial;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MpmModoUsoPrescProced;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimento;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.model.ScoMaterial;

/**
 * VO utilizado na tela de Manter PrescricaoProcedimento para transferencia e manipulacao dos dados.<br>
 * 
 * @author rcorvalao
 */
public class ProcedimentoEspecialVO implements Serializable {
	
	
/**
	 * 
	 */
	private static final long serialVersionUID = 3826285065780585045L;


	//	select PED.IND_SITUACAO  DSP_IND_SITUACAO3  /* CG$FK */
//    ,PED.IND_PERM_PRESCRICAO  DSP_IND_PERM_PRESCRICAO
//    ,PED.SEQ  PED_SEQ
//    ,PED.DESCRICAO  DSP_DESCRICAO
//    from   MPM_PROCED_ESPECIAL_DIVERSOS PED
//    where  (((:SYSTEM.MODE = 'ENTER-QUERY'))
//    OR ((:SYSTEM.MODE = 'NORMAL')
//    and    PED.IND_SITUACAO = 'A' and PED.IND_PERM_PRESCRICAO = 'S'))
//	order by  PED.DESCRICAO asc
	/**
	 * SuggestionBox: Usado na suggestionBox Especiais Diversos
	 */
	private MpmProcedEspecialDiversos procedEspecial;

	
//	select PCI.IND_SITUACAO  DSP_IND_SITUACAO4  /* CG$FK */
//    ,PCI.SEQ  PCI_SEQ
//    ,PCI.DESCRICAO  DSP_DESCRICAO2
//    from   MBC_PROCEDIMENTO_CIRURGICOS PCI
//    where  (((:SYSTEM.MODE = 'ENTER-QUERY'))
//    OR ((:SYSTEM.MODE = 'NORMAL')
//    and    (PCI.IND_PROC_REALIZADO_LEITO = 'S'  and PCI.IND_SITUACAO = 'A')))
//	order by  PCI.DESCRICAO asc
	/**
	 * SuggestionBox: Usado na SB de procedimentos Cirugicos Realizados no Leito
	 * Tabela: MBC_PROCEDIMENTO_CIRURGICOS
	 * Campos: MBC_PROCEDIMENTO_CIRURGICOS.seq, MBC_PROCEDIMENTO_CIRURGICOS.descricao
	 */
	private MbcProcedimentoCirurgicos procedCirugRealizadosLeito; 

	
//	 select MAT.IND_SITUACAO  DSP_IND_SITUACAO
//     ,MAT.CODIGO  MAT_CODIGO
//     ,MAT.NOME  MAT_NOME
//     ,MAT.UMD_CODIGO  DSP_UMD_CODIGO
//		from   SCO_MATERIAIS MAT
//		where  (((:SYSTEM.MODE = 'ENTER-QUERY'))
//		OR ((:SYSTEM.MODE = 'NORMAL')
//		and    (MAT.GMT_CODIGO = :GLOBAL.MPM$CAD_MBP_GRPO_ORT_PROT  and MAT.IND_SITUACAO = 'A')))
	/**
	 * SuggestionBox: Usado na SB de Órteses e Próteses
	 * Tabela: SCO_MATERIAIS
	 * Filtro em: GMT_CODIGO
	 * Campos: SCO_MATERIAIS.codigo, SCO_MATERIAIS.nome, SCO_MATERIAIS.umd_codigo
	 */
	private ScoMaterial orteseProtese;

	
	private MpmPrescricaoProcedimento procedimento;
	
	/**
	 * Campos que deve ser preenchido conforme a suggesntionBox selecionada
	 * 
	 */
	private String unidade;
	
	/**
	 * mpmPrescricaoProcedimento.quantidade
	 */
	private Short quantidade;
	/**
	 * mpmPrescricaoProcedimento.informacaoComplementar
	 */
	private String informacaoComplementar;
	/**
	 * mpmPrescricaoProcedimento.justificativa
	 */
	private String justificativa;
	/**
	 * mpmPrescricaoProcedimento.duracaoTratamentoSolicitado
	 */
	private Short duracaoSolicitada;
	
	private DominioTipoProcedimentoEspecial tipo;
	
	private List<ModoUsoProcedimentoEspecialVO> listaModoUsoProdedimentoEspecialVO;
	
	
	public ProcedimentoEspecialVO() {
		super();
	}


	public void setQuantidade(Short quantidade) {
		this.quantidade = quantidade;
	}


	public Short getQuantidade() {
		return quantidade;
	}


	public void setInformacaoComplementar(String informacaoComplementar) {
		this.informacaoComplementar = informacaoComplementar;
	}


	public String getInformacaoComplementar() {
		return informacaoComplementar;
	}


	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}


	public String getJustificativa() {
		return justificativa;
	}


	public void setDuracaoSolicitada(Short duracaoSolicitada) {
		this.duracaoSolicitada = duracaoSolicitada;
	}


	public Short getDuracaoSolicitada() {
		return duracaoSolicitada;
	}


	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}


	public String getUnidade() {
		return unidade;
	}


	public void setListaModoUsoProdedimentoEspecialVO(List<ModoUsoProcedimentoEspecialVO> listaModoUsoProdedimentoEspecialVO) {
		this.listaModoUsoProdedimentoEspecialVO = listaModoUsoProdedimentoEspecialVO;
	}


	public List<ModoUsoProcedimentoEspecialVO> getListaModoUsoProdedimentoEspecialVO() {
		if (listaModoUsoProdedimentoEspecialVO == null) {
			listaModoUsoProdedimentoEspecialVO = new LinkedList<ModoUsoProcedimentoEspecialVO>();
		}
		return this.listaModoUsoProdedimentoEspecialVO;
	}


	public DominioTipoProcedimentoEspecial getTipo() {
		return tipo;
	}
	public void setTipo(DominioTipoProcedimentoEspecial t) {
		this.tipo = t;
	}

	/**
	 * Metodo utilizado para inicializar o VO com os valores de procedimento.
	 * 
	 * @param umPprocedimento
	 */
	public void setModel(MpmPrescricaoProcedimento umPprocedimento) {
		this.procedimento = umPprocedimento;
		
		this.setDuracaoSolicitada(this.procedimento.getDuracaoTratamentoSolicitado());
		this.setInformacaoComplementar(procedimento.getInformacaoComplementar());
		this.setJustificativa(procedimento.getJustificativa());
		
		if (this.procedimento.getMatCodigo() != null) {
			this.setOrteseProtese(this.procedimento.getMatCodigo());
			this.setQuantidade(procedimento.getQuantidade());
			this.tipo = DominioTipoProcedimentoEspecial.ORTESES_PROTESES;
		} else if (this.procedimento.getProcedimentoCirurgico() != null) {
			this.setProcedCirugRealizadosLeito(this.procedimento.getProcedimentoCirurgico());
			this.tipo = DominioTipoProcedimentoEspecial.PROCEDIMENTOS_REALIZADOS_NO_LEITO;
		} else if (this.procedimento.getProcedimentoEspecialDiverso() != null) {
			this.setProcedEspecial(this.procedimento.getProcedimentoEspecialDiverso());
			this.tipo = DominioTipoProcedimentoEspecial.ESPECIAIS_DIVERSOS;
		}
	}
	
	/**
	 * Obtem um MpmPrescricaoProcedimento com as alteracoes feitas na tela. 
	 * 
	 * @return
	 */
	public MpmPrescricaoProcedimento getModel() {
	    MpmPrescricaoProcedimento procedimentoNovo = this.procedimento == null ? new MpmPrescricaoProcedimento()
        		: this.procedimento;
		
		procedimentoNovo.setDuracaoTratamentoSolicitado(this.getDuracaoSolicitada());
		procedimentoNovo.setInformacaoComplementar(this.getInformacaoComplementar());
		procedimentoNovo.setJustificativa(this.getJustificativa());
		
		if (DominioTipoProcedimentoEspecial.ORTESES_PROTESES == this.getTipo()) {
			procedimentoNovo.setMatCodigo(this.getOrteseProtese());
			procedimentoNovo.setQuantidade(this.getQuantidade());
			
			procedimentoNovo.setProcedimentoCirurgico(null);
			procedimentoNovo.setProcedimentoEspecialDiverso(null);			
			
		} else if (DominioTipoProcedimentoEspecial.PROCEDIMENTOS_REALIZADOS_NO_LEITO == this.getTipo()) {
			procedimentoNovo.setProcedimentoCirurgico(this.getProcedCirugRealizadosLeito());
			
			procedimentoNovo.setMatCodigo(null);
			procedimentoNovo.setQuantidade(null);
			procedimentoNovo.setProcedimentoEspecialDiverso(null);			
			
		} else if (this.isProcedimentoEspecialDiverso()) {
			procedimentoNovo.setProcedimentoEspecialDiverso(this.getProcedEspecial());
			
			procedimentoNovo.setMatCodigo(null);
			procedimentoNovo.setQuantidade(null);
			procedimentoNovo.setProcedimentoCirurgico(null);
			
		}
		
		return procedimentoNovo;
	}
	
	public boolean isProcedimentoEspecialDiverso() {
		return (DominioTipoProcedimentoEspecial.ESPECIAIS_DIVERSOS == this.getTipo());
	}


	public void setProcedEspecial(MpmProcedEspecialDiversos procedEspecial) {
		this.procedEspecial = procedEspecial;
	}


	public MpmProcedEspecialDiversos getProcedEspecial() {
		return procedEspecial;
	}


	public void setProcedCirugRealizadosLeito(MbcProcedimentoCirurgicos procedCirugRealizadosLeito) {
		this.procedCirugRealizadosLeito = procedCirugRealizadosLeito;
	}


	public MbcProcedimentoCirurgicos getProcedCirugRealizadosLeito() {
		return procedCirugRealizadosLeito;
	}


	public void setOrteseProtese(ScoMaterial orteseProtese) {
		this.orteseProtese = orteseProtese;
		this.doSetUnidade(orteseProtese);
	}


	private void doSetUnidade(ScoMaterial umMaterial) {
		if (umMaterial != null) {
			this.setUnidade(umMaterial.getUmdCodigo());
		} else {
			this.setUnidade(null);
		}
	}
	
	public ScoMaterial getOrteseProtese() {
		return orteseProtese;
	}


	public void addAllModoUso(List<MpmModoUsoPrescProced> modoUsoPrescricaoProcedimentos) {
		for (MpmModoUsoPrescProced mpmModoUsoPrescProced : modoUsoPrescricaoProcedimentos) {
			ModoUsoProcedimentoEspecialVO modoUsoVO = new ModoUsoProcedimentoEspecialVO();
			
			modoUsoVO.setModel(mpmModoUsoPrescProced);
			
			this.getListaModoUsoProdedimentoEspecialVO().add(modoUsoVO);
		}
	}
	
	public boolean isEmpty(){
		return procedimento == null;
	}
	
	
}
