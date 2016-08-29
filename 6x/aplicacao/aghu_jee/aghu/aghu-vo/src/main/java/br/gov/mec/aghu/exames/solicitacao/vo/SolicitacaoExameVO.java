package br.gov.mec.aghu.exames.solicitacao.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioOrigemSolicitacao;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTelaOriginouSolicitacaoExame;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * Representa a solicitacao de exame na tela de cadastro de Soliciticao de Exame.
 * 
 * @author rcorvalao
 *
 */


@SuppressWarnings({"PMD.ProperCloneImplementation"})
public class SolicitacaoExameVO implements Serializable, Cloneable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5805992402137014218L;

	/**
	 * Quando for uma edicao esta propriedade deve estar preenchida com a entidade em edicao.
	 * Atributo central deste VO. NAO deve ter get e set. Usar setmodel e getmodel.
	 * TODO setar quando implementar a edicao.
	 */
	private AelSolicitacaoExames solicEx = null;
	
	private SolicitacaoExameItemVO exameItemVo;
	
	private boolean alertaExamesColetaEspecial;
	
	/**
	 * Campo calculado conforme a associacao existente em AelAtendimentoDiversos
	 * atendimentoDiverso.descricaoAtendimentoDiverso
	 */
	private String descricaoAtendimentoDiverso;
	
	
	/**
	 * atendimento.consulta.numero<br>
	 * Digite o número da consulta.
	 */
	private Integer numeroConsulta;
	/**
	 * ComboBox
	 * atendimento.unidadeFuncional
	 * Selecione a unidade de internação solicitante.
	 */
	private AghUnidadesFuncionais unidadeFuncional;
	/**
	 * ComboBox
	 * aelSolicitacaoExames.servidorEhResponsabilid
	 */
	private RapServidores responsavel;
	/**
	 * aelSolicitacaoExames.informacoesClinicas
	 */
	private String informacoesClinicas;
	/**
	 * aelSolicitacaoExames.usaAntimicrobianos
	 */
	private DominioSimNao usaAntimicrobianos;
	/**
	 * aelSolicitacaoExames.indObjetivoSolic
	 */
	private DominioOrigemSolicitacao indObjetivoSolic;
	/**
	 * aelSolicitacaoExames.indTransplante
	 */
	private DominioSimNao indTransplante;
	/**
	 * Indicativo se eh um Atendimento ou AtendimentoDiverso.
	 */
	private Boolean ehDiverso;
	
	/**
	 * aelSolicitacaoExames.atendimentoDiverso
	 */
	private AelAtendimentoDiversos atendimentoDiverso;
	
	/**
	 * Campos Detalhe Solicitação Exame:
	 */
	private String unidadeSolicitante;
	private String recemNascido;
	private Date dthrProgramada;
	
	private Boolean mostrarIndicadorTransplantado;
	private Boolean isSus;
	
	/**
	 * Sequencial dos ItensSolicitacao apenas pra apresentacao na tela.
	 * O seqp do item é gerado no DAO.
	 */
	private int countSequencial = 0;
	
	private List<ItemSolicitacaoExameVO> itemSolicitacaoExameVos;
	
	private AghUnidadesFuncionais unidadeFuncionalAreaExecutora;
	
	
	/**
	 * Unidade de trabalho do usuário realizando a solicitação.
	 */
	private AghUnidadesFuncionais unidadeTrabalho;
	
	/**
	 * Determina onde foi originada (tela/setor/módulo) a solicitação 
	 */
	private DominioTelaOriginouSolicitacaoExame telaOriginouSolicitacao;
	
	/**
	 * Indica se o ticket do paciente foi impresso 
	 */
	private boolean imprimiuTicketPaciente;
	
	private boolean imprimirQuestionario;
	
	private Integer seqSolicitacaoSalva;
	
	private boolean geraSolicExameSitPendente;
	
	private Short unfSeq;
	
	public SolicitacaoExameVO() {
	}
	
	public SolicitacaoExameVO(SolicitacaoExameItemVO itemVO) {
		this.exameItemVo=itemVO;
	}
	
	public SolicitacaoExameVO(AghAtendimentos atd) {
		this.exameItemVo = new SolicitacaoExameItemVO(atd);
		this.setEhDiverso(false);
		
		// Atributos comuns entre atendimento e atendimento diverso.
		this.setMostrarIndicadorTransplantado(
				(atd.getConvenioSaudePlano() != null
						&& atd.getConvenioSaudePlano().getConvenioSaude() != null
						&& DominioGrupoConvenio.S == atd.getConvenioSaudePlano().getConvenioSaude().getGrupoConvenio())
			&&
				(DominioOrigemAtendimento.X == atd.getOrigem())
		);
		
		// Atributo especificos do Atendimento.
		if (atd.getConsulta() != null) {
			this.setNumeroConsulta(atd.getConsulta().getNumero());
		}
		this.setUnidadeFuncional(atd.getUnidadeFuncional());
	
	}
	
	public SolicitacaoExameVO(AelAtendimentoDiversos atdDiverso) {
		this.exameItemVo = new SolicitacaoExameItemVO(atdDiverso);
		this.setEhDiverso(true);
		
		// Atributos comuns entre atendimento e atendimento diverso.
		this.setMostrarIndicadorTransplantado(
				Boolean.FALSE // Por hora nao mostrar este campo para atendimento diverso.
		);
		
		this.setAtendimentoDiverso(atdDiverso);
	}

	public SolicitacaoExameVO(AtendimentoSolicExameVO vo) {
	    this.exameItemVo = new SolicitacaoExameItemVO(vo);
		this.setEhDiverso(false);
		
		// Atributos comuns entre atendimento e atendimento diverso.
		if (vo.getIndTransplantado() != null){
		    this.setMostrarIndicadorTransplantado(Boolean.valueOf(vo.getIndTransplantado()));
		}
		
		// Atributo especificos do Atendimento.
		this.setNumeroConsulta(vo.getNumeroConsulta());
		setUnfSeq(vo.getUnfSeq());
		
		if (vo.getResponsavel() != null){
		    String[] servidor = vo.getResponsavel().split("-");
		    
		    Integer matricula = Integer.valueOf(servidor[0]);
		    Short vinCodigo = Short.valueOf(servidor[1]);
		    RapServidoresId id=  new RapServidoresId(matricula, vinCodigo);
		    RapServidores rsrv = new RapServidores(id);
		    
		    this.setResponsavel(rsrv);
		}
		if (vo.getIsSus() != null){
		    setIsSus(Boolean.valueOf(vo.getIsSus()));
		}
		
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		super.clone();
		SolicitacaoExameVO copia = new SolicitacaoExameVO();

		copia.setSolicEx(getModel());
		copia.setExameItemVo((SolicitacaoExameItemVO) ItemSolicitacaoExameVO.clonar(exameItemVo));
		copia.setAlertaExamesColetaEspecial(alertaExamesColetaEspecial);
		copia.setDescricaoAtendimentoDiverso(descricaoAtendimentoDiverso);
		copia.setNumeroConsulta(numeroConsulta);
		copia.setUnidadeFuncional((AghUnidadesFuncionais) ItemSolicitacaoExameVO.clonar(unidadeFuncional));
		copia.setResponsavel((RapServidores) ItemSolicitacaoExameVO.clonar(responsavel));
		copia.setInformacoesClinicas(informacoesClinicas);
		copia.setUsaAntimicrobianos(usaAntimicrobianos);
		copia.setIndObjetivoSolic(indObjetivoSolic);
		copia.setIndTransplante(indTransplante);
		copia.setEhDiverso(ehDiverso);
		copia.setAtendimentoDiverso((AelAtendimentoDiversos) ItemSolicitacaoExameVO.clonar(atendimentoDiverso));
		copia.setUnidadeSolicitante(unidadeSolicitante);
		copia.setRecemNascido(recemNascido);
		copia.setDthrProgramada(dthrProgramada);
		copia.setMostrarIndicadorTransplantado(mostrarIndicadorTransplantado);
		copia.setCountSequencial(countSequencial);
		copia.setUnidadeTrabalho((AghUnidadesFuncionais) ItemSolicitacaoExameVO.clonar(unidadeTrabalho));
		copia.setUnidadeFuncionalAreaExecutora((AghUnidadesFuncionais) ItemSolicitacaoExameVO.clonar(unidadeFuncionalAreaExecutora));
		copia.setTelaOriginouSolicitacao(telaOriginouSolicitacao);
		copia.setImprimiuTicketPaciente(imprimiuTicketPaciente);
		copia.setImprimirQuestionario(imprimirQuestionario);
		copia.setSeqSolicitacaoSalva(seqSolicitacaoSalva);
		copia.setGeraSolicExameSitPendente(geraSolicExameSitPendente);

		copia.setItemSolicitacaoExameVos(ItemSolicitacaoExameVO.clonarLista(itemSolicitacaoExameVos));

		return copia;
	}

	public String prontuarioFormatado() {
		String retorno = "";
		if(exameItemVo != null && exameItemVo.getProntuario() != null){
			retorno = CoreUtil.formataProntuario(exameItemVo.getProntuario());
		}
		return retorno;
	}
	
	public void doSetSolicitacaoExame(AelSolicitacaoExames soe) {
		this.solicEx = soe;
	}
	
	public Integer getSolicitacaoExameSeq() {
		return this.solicEx.getSeq();
	}
	
	/**
	 * Sequencial dos ItensSolicitacao apenas pra apresentacao na tela.
	 * O seqp do item é gerado no DAO.
	 */
	public Short proximoSequencial() {
		countSequencial = countSequencial + 1;
		return (short) countSequencial;
	}
	
	public boolean isInsercao() {
		return (this.solicEx == null || this.solicEx.getSeq() == null);
	}
	
	private AelSolicitacaoExames getModelSolicitacao() {
		if (solicEx == null) {
			solicEx = new AelSolicitacaoExames();
		}
		
		solicEx.setAtendimento(this.getAtendimento());
		if(this.getIndTransplante() != null){
			solicEx.setIndTransplante(this.getIndTransplante().isSim());
		}
		solicEx.setIndObjetivoSolic(this.getIndObjetivoSolic());
		solicEx.setInformacoesClinicas(this.getInformacoesClinicas());
		solicEx.setServidorResponsabilidade(this.getResponsavel());
		solicEx.setUnidadeFuncional(this.getUnidadeFuncional());
		if(this.getUsaAntimicrobianos() != null){
			solicEx.setUsaAntimicrobianos(this.getUsaAntimicrobianos().isSim());
		}
		solicEx.setAtendimentoDiverso(this.getAtendimentoDiverso());
		solicEx.setRecemNascido(false);
		solicEx.setUnidadeFuncionalAreaExecutora(this.getUnidadeTrabalho());
		return solicEx;
	}
	
	/**
	 * Soh pode ser chamado uma vez durante o processo de Insert ou update.
	 * 
	 * @return
	 */
	public AelSolicitacaoExames getModel() {
		AelSolicitacaoExames solicEx = this.getModelSolicitacao();
		
		for (ItemSolicitacaoExameVO itemVO : this.getItemSolicitacaoExameVos()) {
			// Adiciona apenas os itens de solicitacaoExame, descarta os dependentes (obrigatorios e opcionais).
			if (itemVO.getSequencial() != null) {
				AelItemSolicitacaoExames item = itemVO.getModel();
				solicEx.addItemSolicitacaoExame(item);
				
				// Para cada dependente OBRIGATORIO adiciona e associa ao item pai.
				List<ItemSolicitacaoExameVO> dependentesObrigratorios = itemVO.getDependentesObrigratorios();
				for (ItemSolicitacaoExameVO dependenteVO : dependentesObrigratorios) {
					AelItemSolicitacaoExames dependente = dependenteVO.getModel();
					item.addItemSolicitacaoExame(dependente);
				}
				
				// Para cada dependente OPCIONAL adiciona e associa ao item pai.
				List<ItemSolicitacaoExameVO> dependentesObrigatorios = itemVO.getDependentesOpcionais();
				for (ItemSolicitacaoExameVO dependenteVO : dependentesObrigatorios) {
					if (dependenteVO.getExameOpcionalSelecionado()) {
						AelItemSolicitacaoExames dependente = dependenteVO.getModel();
						item.addItemSolicitacaoExame(dependente);
					}
				}
			}//IF
		}
		
		return solicEx;
	}
	
	
	public Date getDthrProgramada() {
		return dthrProgramada;
	}

	public void setDthrProgramada(Date dthrProgramada) {
		this.dthrProgramada = dthrProgramada;
	}

	public String getRecemNascido() {
		return recemNascido;
	}

	public void setRecemNascido(String recemNascido) {
		this.recemNascido = recemNascido;
	}
	
	public String getUnidadeSolicitante() {
		return unidadeSolicitante;
	}

	public void setUnidadeSolicitante(String unidadeSolicitante) {
		this.unidadeSolicitante = unidadeSolicitante;
	}

	/**
	 * @return the numeroConsulta
	 */
	public Integer getNumeroConsulta() {
		return numeroConsulta;
	}

	/**
	 * @param numeroConsulta the numeroConsulta to set
	 */
	public void setNumeroConsulta(Integer numeroConsulta) {
		this.numeroConsulta = numeroConsulta;
	}

	/**
	 * Selecione a unidade de internação solicitante.
	 * @return the unidadeFuncional
	 */
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	/**
	 * Selecione a unidade de internação solicitante.
	 * @param unidadeFuncional the unidadeFuncional to set
	 */
	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	/**
	 * @return the responsavel
	 */
	public RapServidores getResponsavel() {
		return responsavel;
	}

	/**
	 * @param responsavel the responsavel to set
	 */
	public void setResponsavel(RapServidores responsavel) {
		this.responsavel = responsavel;
	}

	/**
	 * @return the informacoesClinicas
	 */
	public String getInformacoesClinicas() {
		return informacoesClinicas;
	}

	/**
	 * @param informacoesClinicas the informacoesClinicas to set
	 */
	public void setInformacoesClinicas(String informacoesClinicas) {
		this.informacoesClinicas = informacoesClinicas;
	}

	/**
	 * @return the usaAntimicrobianos
	 */
	public DominioSimNao getUsaAntimicrobianos() {
		return usaAntimicrobianos;
	}

	/**
	 * @param usaAntimicrobianos the usaAntimicrobianos to set
	 */
	public void setUsaAntimicrobianos(DominioSimNao q) {
		this.usaAntimicrobianos = q;
	}

	/**
	 * @return the indObjetivoSolic
	 */
	public DominioOrigemSolicitacao getIndObjetivoSolic() {
		return indObjetivoSolic;
	}

	/**
	 * @param indObjetivoSolic the indObjetivoSolic to set
	 */
	public void setIndObjetivoSolic(DominioOrigemSolicitacao indObjetivoSolic) {
		this.indObjetivoSolic = indObjetivoSolic;
	}

	/**
	 * @return the indTransplante
	 */
	public DominioSimNao getIndTransplante() {
		return indTransplante;
	}

	/**
	 * @param indTransplante the indTransplante to set
	 */
	public void setIndTransplante(DominioSimNao q) {
		this.indTransplante = q;
	}

	/**
	 * @param ehDiverso the ehDiverso to set
	 */
	public void setEhDiverso(Boolean ehDiverso) {
		this.ehDiverso = ehDiverso;
	}

	/**
	 * @return the ehDiverso
	 */
	public Boolean getEhDiverso() {
		return ehDiverso;
	}


    public Integer getAtendimentoDiversoSeq() {
        return atendimentoDiverso != null ? atendimentoDiverso.getSeq() : null;
    }

	public void setAtendimentoDiverso(AelAtendimentoDiversos atendimentoDiverso) {
		this.atendimentoDiverso = atendimentoDiverso;
	}
	
	public AelAtendimentoDiversos getAtendimentoDiverso() {
		return atendimentoDiverso;
	}

	/**
	 * @param descricaoAtendimentoDiverso the descricaoAtendimentoDiverso to set
	 */
	public void setDescricaoAtendimentoDiverso(
			String descricaoAtendimentoDiverso) {
		this.descricaoAtendimentoDiverso = descricaoAtendimentoDiverso;
	}

	/**
	 * @return the descricaoAtendimentoDiverso
	 */
	public String getDescricaoAtendimentoDiverso() {
		return descricaoAtendimentoDiverso;
	}
	
	/**
	 * @param mostrarIndicadorTransplantado the mostrarIndicadorTransplantado to set
	 */
	public void setMostrarIndicadorTransplantado(
			Boolean mostrarIndicadorTransplantado) {
		this.mostrarIndicadorTransplantado = mostrarIndicadorTransplantado;
	}

	/**
	 * @return the mostrarIndicadorTransplantado
	 */
	public Boolean getMostrarIndicadorTransplantado() {
		return mostrarIndicadorTransplantado;
	}

	public Integer getCodPaciente() {
		return exameItemVo.getCodPaciente();
	}

	public void setCodPaciente(Integer codPaciente) {
		exameItemVo.setCodPaciente(codPaciente);
	}

	public AinLeitos getLeito() {
		return exameItemVo.getLeito();
	}

	public void setLeito(AinLeitos leito) {
		exameItemVo.setLeito(leito);
	}

	public Integer getProntuario() {
		return exameItemVo.getProntuario();
	}

	public void setProntuario(Integer prontuario) {
		exameItemVo.setProntuario(prontuario);
	}

	public String getNomePaciente() {
		return exameItemVo.getNomePaciente();
	}

	public void setNomePaciente(String nomePaciente) {
		exameItemVo.setNomePaciente(nomePaciente);
	}
	
	public String getNomeSocialPaciente() {
		return exameItemVo.getNomeSocialPaciente();
	}

	public void setNomeSocialPaciente(String nomeSocialPaciente) {
		exameItemVo.setNomeSocialPaciente(nomeSocialPaciente);
	}

	public DominioOrigemAtendimento getOrigem() {
		return exameItemVo.getOrigem();
	}

	public void setOrigem(DominioOrigemAtendimento origem) {
		exameItemVo.setOrigem(origem);
	}

	public Integer getIdade() {
		return exameItemVo.getIdade();
	}

	public void setIdade(Integer idade) {
		exameItemVo.setIdade(idade);
	}

	public Date getDataAtendimento() {
		return exameItemVo.getDataAtendimento();
	}

	public void setDataAtendimento(Date dataAtendimento) {
		exameItemVo.setDataAtendimento(dataAtendimento);
	}

	public String getNomeEspecialidade() {
		return exameItemVo.getNomeEspecialidade();
	}

	public void setNomeEspecialidade(String nomeEspecialidade) {
		exameItemVo.setNomeEspecialidade(nomeEspecialidade);
	}

	public String getLocalDescricao() {
		return exameItemVo.getLocalDescricao();
	}

	public void setLocalDescricao(String localDescricao) {
		exameItemVo.setLocalDescricao(localDescricao);
	}

	public void setAtendimentoSeq(Integer atendimentoSeq) {
		exameItemVo.setAtendimentoSeq(atendimentoSeq);
	}

	public Integer getAtendimentoSeq() {
		return exameItemVo.getAtendimentoSeq();
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		exameItemVo.setAtendimento(atendimento);
	}

	public AghAtendimentos getAtendimento() {
		if(exameItemVo != null) {
			return exameItemVo.getAtendimento();
		}
		return null;
	}

	/**
	 * @param itemSolicitacaoExameVos the itemSolicitacaoExameVos to set
	 */
	public void setItemSolicitacaoExameVos(List<ItemSolicitacaoExameVO> itemSolicitacaoExameVos) {
		this.itemSolicitacaoExameVos = itemSolicitacaoExameVos;
	}

	/**
	 * @return the itemSolicitacaoExameVos
	 */
	public List<ItemSolicitacaoExameVO> getItemSolicitacaoExameVos() {
		if (this.itemSolicitacaoExameVos == null) {
			this.itemSolicitacaoExameVos = new LinkedList<ItemSolicitacaoExameVO>();
		}
		return itemSolicitacaoExameVos;
	}
	
	public void setUnidadeTrabalho(AghUnidadesFuncionais unidadeTrabalho) {
		this.unidadeTrabalho = unidadeTrabalho;
	}
	
	public AghUnidadesFuncionais getUnidadeTrabalho() {
		return unidadeTrabalho;
	}

	public void setTelaOriginouSolicitacao(DominioTelaOriginouSolicitacaoExame telaOriginouSolicitacao) {
		this.telaOriginouSolicitacao = telaOriginouSolicitacao;
	}
	
	public DominioTelaOriginouSolicitacaoExame getTelaOriginouSolicitacao() {
		return telaOriginouSolicitacao;
	}
	
	public void setImprimiuTicketPaciente(boolean imprimiuTicketPaciente) {
		this.imprimiuTicketPaciente = imprimiuTicketPaciente;
	}
	
	public Boolean getImprimiuTicketPaciente() {
		return imprimiuTicketPaciente;
	}
	
	public boolean isOrigemAtendimentoExterno() {
		return getOrigem() == DominioOrigemAtendimento.X;
	}
	
	public Integer getSeqSolicitacaoSalva() {
		return seqSolicitacaoSalva;
	}
	
	public void setSeqSolicitacaoSalva(Integer seqSolicitacaoSalva) {
		this.seqSolicitacaoSalva = seqSolicitacaoSalva;
	}

	public Boolean getAlertaExamesColetaEspecial() {
		return alertaExamesColetaEspecial;
	}

	public void setAlertaExamesColetaEspecial(boolean alertaExamesColetaEspecial) {
		this.alertaExamesColetaEspecial = alertaExamesColetaEspecial;
	}

	public void setImprimirQuestionario(boolean imprimirQuestionario) {
		this.imprimirQuestionario = imprimirQuestionario;
	}

	public boolean isImprimirQuestionario() {
		return imprimirQuestionario;
	}
	
	
	public String getSeqSolicitacaoSalvaStr() {
		if(this.seqSolicitacaoSalva != null) {
			return this.seqSolicitacaoSalva.toString();
		}
		return null;
	}

	public boolean isGeraSolicExameSitPendente() {
		return geraSolicExameSitPendente;
	}

	public void setGeraSolicExameSitPendente(boolean geraSolicExameSitPendente) {
		this.geraSolicExameSitPendente = geraSolicExameSitPendente;
	}

	public void setSolicEx(AelSolicitacaoExames solicEx) {
		this.solicEx = solicEx;
	}

	public SolicitacaoExameItemVO getExameItemVo() {
		return exameItemVo;
	}

	public void setExameItemVo(SolicitacaoExameItemVO exameItemVo) {
		this.exameItemVo = exameItemVo;
	}

	public int getCountSequencial() {
		return countSequencial;
	}

	public void setCountSequencial(int countSequencial) {
		this.countSequencial = countSequencial;
	}

	
	public Short getUnfSeq() {
	    return unfSeq;
	}

	
	public void setUnfSeq(Short unfSeq) {
	    this.unfSeq = unfSeq;
	}

	
	public Boolean getIsSus() {
	    return isSus;
	}

	
	public void setIsSus(Boolean isSus) {
	    this.isSus = isSus;
	}

	public AghUnidadesFuncionais getUnidadeFuncionalAreaExecutora() {
		return unidadeFuncionalAreaExecutora;
	}

	public void setUnidadeFuncionalAreaExecutora(
			AghUnidadesFuncionais unidadeFuncionalAreaExecutora) {
		this.unidadeFuncionalAreaExecutora = unidadeFuncionalAreaExecutora;
	}
}