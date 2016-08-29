package br.gov.mec.aghu.action.converter;


import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.internacao.action.CadastroInternacaoController;
import br.gov.mec.aghu.internacao.vo.ProfessorCrmInternacaoVO;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.core.converter.AbstractConverter;

@FacesConverter(value = "professorCrmInternacaoVOConverter")
public class ProfessorCrmInternacaoVOConverter extends AbstractConverter {


	/**
	 * 
	 */
	private static final long serialVersionUID = -8330037093800654496L;
	
	@Inject
	private CadastroInternacaoController cadastroInternacaoController;


	@Override
	public Object getAsObject(String valor) {
		if (StringUtils.isBlank(valor)) {
			return null;
		}
		
		List<ProfessorCrmInternacaoVO> result = new ArrayList<ProfessorCrmInternacaoVO>(0);
		result = cadastroInternacaoController.pesquisarProfessor(valor);
		
		if (result.isEmpty()) {
			return null;
		} else {
			return result.get(0);
		}
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String valor)	{
		if (StringUtils.isBlank(valor)) {
			return null;
		}
		
		AghEspecialidades especialidade = (AghEspecialidades)component.getAttributes().get("especialidade"); 
		FatConvenioSaude convenio = (FatConvenioSaude)component.getAttributes().get("convenio");
		List<ProfessorCrmInternacaoVO> result = new ArrayList<ProfessorCrmInternacaoVO>(0);
		
		if(especialidade != null || convenio != null) {
			result = cadastroInternacaoController.pesquisarProfessoresCrm(getSeqEspecialidade(especialidade),
					getSiglaEspecialidade(especialidade), getCodigoconvenio(convenio),
					getVerificaEscala(convenio), valor, null, null);
		}
		else {
			result = cadastroInternacaoController.pesquisarProfessor(valor);
		}
		
		if (result.isEmpty()) {
			return null;
		} else {
			return result.get(0);
		}
	}
	
	@Override
	public String getAsString(FacesContext context, UIComponent component, Object valor) {
		return ((ProfessorCrmInternacaoVO) valor).getNroRegConselho().toString();
	}
	
	private Short getSeqEspecialidade(AghEspecialidades especialidade) {
		return (especialidade != null) ? especialidade.getSeq() : null;
	}
	
	private String getSiglaEspecialidade(AghEspecialidades especialidade) {
		return (especialidade != null) ? especialidade.getSigla() : null;
	}
	
	private Short getCodigoconvenio(FatConvenioSaude convenio) {
		return (convenio != null) ? convenio.getCodigo() : null;
	}
	
	private Boolean getVerificaEscala(FatConvenioSaude convenio) {
		return (convenio != null) ? convenio.getVerificaEscalaProfInt() : null;
	}	
}

