package br.gov.mec.aghu.procedimentoterapeutico.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteAguardandoAtendimentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteEmAtendimentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PacienteAcolhimentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PacienteConcluidoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.RegistroIntercorrenciaVO;
import br.gov.mec.aghu.core.action.ActionController;


public class RegistrarIntercorrenciaController extends ActionController{

	private static final String REGISTRAR_INTERCORRENCIA_CRUD = "procedimentoterapeutico-registrarIntercorrenciaCrud";
	private static final String LISTA_PACIENTE_LIST = "procedimentoterapeutico-listaPacienteList";

	/**
	 * 
	 */
	private static final long serialVersionUID = 3081450180103167881L;

	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	
	@Inject
	private RegistrarIntercorrenciaCrudController registrarIntercorrenciaCrudController;
	
	private Integer codigoPaciente;
	private List<RegistroIntercorrenciaVO> listaIntercorrencias = new ArrayList<RegistroIntercorrenciaVO>();
	private RegistroIntercorrenciaVO itemListaIntercorrencia;
	private String nome;
	private Integer prontuario;
	private Integer seqSessao;
	private ListaPacienteEmAtendimentoVO listaPacienteEmAtendimentoVO = new ListaPacienteEmAtendimentoVO();
	private PacienteConcluidoVO pacienteConcluidoVO = new PacienteConcluidoVO();
	private PacienteAcolhimentoVO pacienteAcolhimentoVO = new PacienteAcolhimentoVO();
	private ListaPacienteAguardandoAtendimentoVO listaPacienteAguardandoAtendimentoVO = new ListaPacienteAguardandoAtendimentoVO();
	private List<ListaPacienteVO> listaIntercorrenciasSelecionadas = new ArrayList<ListaPacienteVO>(); 
	
	private Integer selectAba;
	
	@PostConstruct
	public void iniciar() {	
		this.begin(conversation);
		obterListaIntercorrencia();
	}	
	
	public String voltar(){
		return LISTA_PACIENTE_LIST;
	}
	
	public String novo(){
		registrarIntercorrenciaCrudController.setSessao(seqSessao);
		return REGISTRAR_INTERCORRENCIA_CRUD;
	}
	
	public void obterListaIntercorrencia(){
		if(codigoPaciente != null){
			listaIntercorrencias = procedimentoTerapeuticoFacade.obterRegistroIntercorrenciaPorPaciente(codigoPaciente);
			alterarSelecaoNaListaVO();
		}
	}
	
	public String obterHint(String item, Integer tamanhoMaximo){
		String retorno = item;
		if (retorno.length() > tamanhoMaximo) {
			retorno = StringUtils.abbreviate(retorno, tamanhoMaximo);
		}
		return retorno;
	}
	
	public void insertListSelection(RegistroIntercorrenciaVO item){
		if(selectAba == 4){
			if(listaPacienteEmAtendimentoVO.getListaIntercorrenciasSelecionadas().contains(item)){
				listaPacienteEmAtendimentoVO.getListaIntercorrenciasSelecionadas().remove(item);
				if (listaPacienteEmAtendimentoVO.getListaIntercorrenciasSelecionadas().isEmpty()) {
					listaIntercorrenciasSelecionadas.remove(listaPacienteEmAtendimentoVO);
				}
			}else{
				listaPacienteEmAtendimentoVO.getListaIntercorrenciasSelecionadas().add(item);
				if (!listaIntercorrenciasSelecionadas.contains(listaPacienteEmAtendimentoVO)) {
					listaIntercorrenciasSelecionadas.add(listaPacienteEmAtendimentoVO);
				}
			}
		}else if(selectAba == 2){
			if(pacienteAcolhimentoVO.getListaIntercorrenciasSelecionadas().contains(item)){
				pacienteAcolhimentoVO.getListaIntercorrenciasSelecionadas().remove(item);
				if (pacienteAcolhimentoVO.getListaIntercorrenciasSelecionadas().isEmpty()) {
					listaIntercorrenciasSelecionadas.remove(pacienteAcolhimentoVO);
				}
			}else{
				pacienteAcolhimentoVO.getListaIntercorrenciasSelecionadas().add(item);
				listaIntercorrenciasSelecionadas.add(pacienteAcolhimentoVO);
				if (!listaIntercorrenciasSelecionadas.contains(pacienteAcolhimentoVO)) {
					listaIntercorrenciasSelecionadas.add(pacienteAcolhimentoVO);
				}
			}
		}else if(selectAba == 3){
			if(listaPacienteAguardandoAtendimentoVO.getListaIntercorrenciasSelecionadas().contains(item)){
				listaPacienteAguardandoAtendimentoVO.getListaIntercorrenciasSelecionadas().remove(item);
				if(listaPacienteAguardandoAtendimentoVO.getListaIntercorrenciasSelecionadas().isEmpty()){
					listaIntercorrenciasSelecionadas.remove(listaPacienteAguardandoAtendimentoVO);
				}
			}else{
				listaPacienteAguardandoAtendimentoVO.getListaIntercorrenciasSelecionadas().add(item);
				if (!listaIntercorrenciasSelecionadas.contains(listaPacienteAguardandoAtendimentoVO)) {
					listaIntercorrenciasSelecionadas.add(listaPacienteAguardandoAtendimentoVO);
				}
			}
		}else if(selectAba == 5){
			if(pacienteConcluidoVO.getListaIntercorrenciasSelecionadas().contains(item)){
				pacienteConcluidoVO.getListaIntercorrenciasSelecionadas().remove(item);
				if(pacienteConcluidoVO.getListaIntercorrenciasSelecionadas().isEmpty()){
					listaIntercorrenciasSelecionadas.remove(pacienteConcluidoVO);
				}
			}else{
				pacienteConcluidoVO.getListaIntercorrenciasSelecionadas().add(item);
				if(!listaIntercorrenciasSelecionadas.contains(pacienteConcluidoVO)){
					listaIntercorrenciasSelecionadas.add(pacienteConcluidoVO);
				}
			}
		}
		alterarSelecaoNaListaVO();
		
	}
    private void alterarSelecaoNaListaVO(){
        for(RegistroIntercorrenciaVO vo: listaIntercorrencias){
            if(selectAba == 4){
            	if(listaPacienteEmAtendimentoVO.getListaIntercorrenciasSelecionadas().contains(vo)){
            		vo.setSelecionado(true);
            	}else{
            		vo.setSelecionado(false);
            	}
            }else if(selectAba == 2){
    			if(pacienteAcolhimentoVO.getListaIntercorrenciasSelecionadas().contains(vo)){
    				vo.setSelecionado(true);
                }else{
                    vo.setSelecionado(false);
                }
    		}else if(selectAba == 3){
    			if(listaPacienteAguardandoAtendimentoVO.getListaIntercorrenciasSelecionadas().contains(vo)){
    				vo.setSelecionado(true);
                }else{
                    vo.setSelecionado(false);
                }
    		}else if(selectAba == 5){
    			if(pacienteConcluidoVO.getListaIntercorrenciasSelecionadas().contains(vo)){
    				vo.setSelecionado(true);
                }else{
                    vo.setSelecionado(false);
                }
    		}
        }
    }

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}
	
	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public List<RegistroIntercorrenciaVO> getListaIntercorrencias() {
		return listaIntercorrencias;
	}

	public void setListaIntercorrencias(
			List<RegistroIntercorrenciaVO> listaIntercorrencias) {
		this.listaIntercorrencias = listaIntercorrencias;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public RegistroIntercorrenciaVO getItemListaIntercorrencia() {
		return itemListaIntercorrencia;
	}

	public void setItemListaIntercorrencia(
			RegistroIntercorrenciaVO itemListaIntercorrencia) {
		this.itemListaIntercorrencia = itemListaIntercorrencia;
	}

	public Integer getSeqSessao() {
		return seqSessao;
	}

	public void setSeqSessao(Integer seqSessao) {
		this.seqSessao = seqSessao;
	}

	public ListaPacienteEmAtendimentoVO getListaPacienteEmAtendimentoVO() {
		return listaPacienteEmAtendimentoVO;
	}

	public void setListaPacienteEmAtendimentoVO(
			ListaPacienteEmAtendimentoVO listaPacienteEmAtendimentoVO) {
		this.listaPacienteEmAtendimentoVO = listaPacienteEmAtendimentoVO;
	}

	public PacienteConcluidoVO getPacienteConcluidoVO() {
		return pacienteConcluidoVO;
	}

	public void setPacienteConcluidoVO(PacienteConcluidoVO pacienteConcluidoVO) {
		this.pacienteConcluidoVO = pacienteConcluidoVO;
	}

	public PacienteAcolhimentoVO getPacienteAcolhimentoVO() {
		return pacienteAcolhimentoVO;
	}

	public void setPacienteAcolhimentoVO(PacienteAcolhimentoVO pacienteAcolhimentoVO) {
		this.pacienteAcolhimentoVO = pacienteAcolhimentoVO;
	}

	public ListaPacienteAguardandoAtendimentoVO getListaPacienteAguardandoAtendimentoVO() {
		return listaPacienteAguardandoAtendimentoVO;
	}

	public void setListaPacienteAguardandoAtendimentoVO(
			ListaPacienteAguardandoAtendimentoVO listaPacienteAguardandoAtendimentoVO) {
		this.listaPacienteAguardandoAtendimentoVO = listaPacienteAguardandoAtendimentoVO;
	}

	public Integer getSelectAba() {
		return selectAba;
	}

	public void setSelectAba(Integer selectAba) {
		this.selectAba = selectAba;
	}

	public List<ListaPacienteVO> getListaIntercorrenciasSelecionadas() {
		return listaIntercorrenciasSelecionadas;
	}

	public void setListaIntercorrenciasSelecionadas(
			List<ListaPacienteVO> listaIntercorrenciasSelecionadas) {
		this.listaIntercorrenciasSelecionadas = listaIntercorrenciasSelecionadas;
	}

} 	