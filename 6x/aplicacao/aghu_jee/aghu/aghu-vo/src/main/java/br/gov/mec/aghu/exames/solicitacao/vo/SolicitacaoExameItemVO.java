package br.gov.mec.aghu.exames.solicitacao.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.core.persistence.BaseEntity;


/**
 * Representa um solicitacao de Exames na listagem da tela de Pesquisa de Solicitacao.
 * 
 * @author rcorvalao
 *
 */
public class SolicitacaoExameItemVO implements BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5751018780945284599L;
	/**
	 * atendimento.seq
	 */
	private Integer atendimentoSeq;
	private AghAtendimentos atendimento;
	
	/**
	 * atendimento.paciente.prontuario<br>
	 */
	private Integer prontuario;
	/**
	 * atendimento.paciente.nome
	 */
	private String nomePaciente;
	/**
	 * atendimento.paciente.nomeSocial
	 */
	private String nomeSocialPaciente;
	/**
	 * atendimento.origem
	 */
	private DominioOrigemAtendimento origem;
	/**
	 * Calculo apartir da data de nascimento
	 * atendicmento.paciente.dtNascimento
	 */
	private Integer idade;
	/**
	 * atendimento.dthrInicio
	 */
	private Date dataAtendimento;
	/**
	 * atendimento.especialidade.nomeEspecialidade
	 */
	private String nomeEspecialidade;
	/**
	 * Calculo entre os campos: AinLeitos, AinQuartos ou AghUnidadesFuncionais. 
	 * atendimento.descricaoLocalizacao
	 */
	private String localDescricao;
	
	/**
	 * atendimento.ainLeitos.leito
	 */
	private AinLeitos leito;
	
	private String leitoID;
	private Integer codPaciente;
	
	public SolicitacaoExameItemVO() {
	}
	
	/**
	 * Usado apenas pela subclasse. Deve ser protected mesmo.
	 * @param atdDiverso
	 */
	public SolicitacaoExameItemVO(AelAtendimentoDiversos atdDiverso) {
		// TODO setar valores 
//		this.setAtendimentoSeq();
//		this.setProntuario();
//		this.setNomePaciente();
//		this.setIdade();
//		this.setDataAtendimento();
//		this.setNomeEspecialidade();
//		this.setOrigem();
//		this.setLocalDescricao();		
	}
	

	public SolicitacaoExameItemVO(Integer atdSeq) {
		this.setAtendimentoSeq(atdSeq);
	}

	public SolicitacaoExameItemVO(AghAtendimentos atd) {
		if (atd == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!");
		}
		this.setAtendimento(atd);
		this.setAtendimentoSeq(atd.getSeq());
		this.setProntuario(atd.getPaciente().getProntuario());
		this.setNomePaciente(atd.getPaciente().getNome());
		this.setNomeSocialPaciente(atd.getPaciente().getNomeSocial());
		this.setIdade(atd.getPaciente().getIdade());
		this.setDataAtendimento(atd.getDthrInicio());
		if (atd.getEspecialidade() != null) {
			this.setNomeEspecialidade(atd.getEspecialidade().getNomeEspecialidade());
		}
		this.setOrigem(atd.getOrigem());
		this.setLocalDescricao(atd.getDescricaoLocalizacao(true));
		this.setLeito(atd.getLeito());
		this.setCodPaciente(atd.getPaciente().getCodigo());
	}

	public SolicitacaoExameItemVO(AtendimentoSolicExameVO vo) {
	    if (vo == null) {
    		throw new IllegalArgumentException("Parametro obrigatorio nao informado!");
    		}
    	    this.setAtendimentoSeq(vo.getAtdSeq());
    	    this.setProntuario(vo.getProntuario());
    	    this.setNomePaciente(vo.getNomePaciente());
    	    this.setIdade(vo.getIdade());
    	    this.setDataAtendimento(vo.getDataAtendimento());
    	    this.setNomeEspecialidade(vo.getNomeEspecialidade());
    	    if (vo.getOrigem() != null){
    		this.setOrigem(DominioOrigemAtendimento.valueOf(vo.getOrigem()));
    	    }
    	    this.setLocalDescricao(vo.getLocalDescricao()); //.substring(0, 7));
    	    if (vo.getLeito() != null){
    		this.setLeitoID(vo.getLeito());
    	    }
    	    this.setCodPaciente(vo.getCodPaciente());
	}

	public Integer getCodPaciente() {
		return codPaciente;
	}

	public void setCodPaciente(Integer codPaciente) {
		this.codPaciente = codPaciente;
	}

	public AinLeitos getLeito() {
		return leito;
	}

	public void setLeito(AinLeitos leito) {
		this.leito = leito;
	}

	/**
	 * @return the prontuario
	 */
	public Integer getProntuario() {
		return prontuario;
	}
	/**
	 * atendimento.paciente.prontuario
	 * @param prontuario the prontuario to set
	 */
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	/**
	 * @return the nomePaciente
	 */
	public String getNomePaciente() {
		return nomePaciente;
	}
	/**
	 *  atendimento.paciente.nome
	 * @param nomePaciente the nomePaciente to set
	 */
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	/**
	 * @return the nomeSocialPaciente
	 */
	public String getNomeSocialPaciente() {
		return nomeSocialPaciente;
	}
	/**
	 *  atendimento.paciente.nomeSocial
	 * @param nomeSocialPaciente the nomeSocialPaciente to set
	 */
	public void setNomeSocialPaciente(String nomeSocialPaciente) {
		this.nomeSocialPaciente = nomeSocialPaciente;
	}
	/**
	 * @return the origem
	 */
	public DominioOrigemAtendimento getOrigem() {
		return origem;
	}
	/**
	 * atendimento.origem
	 * @param origem the origem to set
	 */
	public void setOrigem(DominioOrigemAtendimento origem) {
		this.origem = origem;
	}
	/**
	 * @return the idade
	 */
	public Integer getIdade() {
		return idade;
	}
	/**
	 * Calculo apartir da data de nascimento
	 * atendicmento.paciente.dtNascimento
	 * @param idade the idade to set
	 */
	public void setIdade(Integer idade) {
		this.idade = idade;
	}
	/**
	 * @return the dataAtendimento
	 */
	public Date getDataAtendimento() {
		return dataAtendimento;
	}
	/**
	 * atendimento.dthrInicio
	 * @param dataAtendimento the dataAtendimento to set
	 */
	public void setDataAtendimento(Date dataAtendimento) {
		this.dataAtendimento = dataAtendimento;
	}
	/**
	 * @return the nomeEspecialidade
	 */
	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}
	/**
	 * atendimento.especialidade.nomeEspecialidade
	 * @param nomeEspecialidade the nomeEspecialidade to set
	 */
	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}
	/**
	 * @return the localDescricao
	 */
	public String getLocalDescricao() {
		return localDescricao;
	}
	/**
	 * Calculo entre os campos: AinLeitos, AinQuartos ou AghUnidadesFuncionais. 
	 * atendimento.descricaoLocalizacao
	 * @param localDescricao the localDescricao to set
	 */
	public void setLocalDescricao(String localDescricao) {
		this.localDescricao = localDescricao;
	}

	/**
	 * @param atendimentoSeq the atendimentoSeq to set
	 */
	public void setAtendimentoSeq(Integer atendimentoSeq) {
		this.atendimentoSeq = atendimentoSeq;
	}

	/**
	 * @return the atendimentoSeq
	 */
	public Integer getAtendimentoSeq() {
		return atendimentoSeq;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	public AghAtendimentos getAtendimento() {
		return atendimento;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((atendimentoSeq == null) ? 0 : atendimentoSeq.hashCode());
		result = prime * result
				+ ((codPaciente == null) ? 0 : codPaciente.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}	
		if (obj == null){
			return false;
		}	
		if (!(obj instanceof SolicitacaoExameItemVO)){
			return false;
		}	
		SolicitacaoExameItemVO other = (SolicitacaoExameItemVO) obj;
		if (atendimentoSeq == null) {
			if (other.atendimentoSeq != null){
				return false;
			}	
		} else if (!atendimentoSeq.equals(other.atendimentoSeq)){
			return false;
		}	
		if (codPaciente == null) {
			if (other.codPaciente != null){
				return false;
			}	
		} else if (!codPaciente.equals(other.codPaciente)){
			return false;
		}	
		return true;
	}

	
	public String getLeitoID() {
	    return leitoID;
	}

	
	public void setLeitoID(String leitoID) {
	    this.leitoID = leitoID;
	}

}
