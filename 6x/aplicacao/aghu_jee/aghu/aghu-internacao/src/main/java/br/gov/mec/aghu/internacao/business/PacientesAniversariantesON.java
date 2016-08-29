package br.gov.mec.aghu.internacao.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.PacientesAniversariantesVO;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * 
 * @author bsoliveira
 * 
 */
@Stateless
public class PacientesAniversariantesON extends BaseBusiness {


	private static final Log LOG = LogFactory.getLog(PacientesAniversariantesON.class);

	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}


	@Inject
	private AinInternacaoDAO ainInternacaoDAO;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5082867517580599783L;
	private static final SimpleDateFormat data = new SimpleDateFormat("dd/MM/yyyy");
	
	/**
	 * Realiza a pesquisa de pacientes aniversáriantes.
	 * 
	 * @param {Date} dtReferencia
	 * @return {List} PacientesAniversariantesVO
	 */
	public List<PacientesAniversariantesVO> pesquisaPacientesAniversariantes(Date dtReferencia) {

		List<Object[]> select = getAinInternacaoDAO().pesquisaPacientesAniversariantes(dtReferencia);
		
		List<PacientesAniversariantesVO> retorno = new ArrayList<PacientesAniversariantesVO>();

		// Popula VO.
		Iterator<Object[]> it = select.iterator();
		while (it.hasNext()) {
			Object[] obj = it.next();
			PacientesAniversariantesVO vo = new PacientesAniversariantesVO();
			
			Short unfSeq = getPesquisaInternacaoFacade().buscarUnidadeInternacao((String) obj[0], (Short) obj[1], (Short) obj[2]);
			AghUnidadesFuncionais unidadeFuncional = this.aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(unfSeq);

			// ANDAR
			String andar = unidadeFuncional.getAndar();
			vo.setAndar(andar);

			// ALA
			if (unidadeFuncional.getIndAla() != null) {
				AghAla ala = unidadeFuncional.getIndAla();
				vo.setAla(ala.getDescricao().substring(0, 1));	
			}

			// LEITO
			if (obj[0] != null) {
				vo.setLeito((String) obj[0]);
			}

			// PRONTUÁRIO
			if (obj[3] != null) {
				vo.setProntuario(String.valueOf((Integer) obj[3]));
			}

			// NOME PACIENTE
			if (obj[4] != null) {
				vo.setNomePaciente((String) obj[4]);
			}

			// DATA NASCIMENTO
			if (obj[5] != null) {
				Date dtNascimento = (Date) obj[5];
				vo.setDataNascimento(data.format(dtNascimento));
			}

			retorno.add(vo);

		}
		
		Collections.sort(retorno, new Comparator<PacientesAniversariantesVO>() {
			@Override
			public int compare(PacientesAniversariantesVO o1, PacientesAniversariantesVO o2) {		
					return o1.getAla().compareTo(o2.getAla());				
			}
		});

		return retorno;
	}
	
	protected AinInternacaoDAO getAinInternacaoDAO(){
		return ainInternacaoDAO;
	}
		
	protected IPesquisaInternacaoFacade getPesquisaInternacaoFacade(){
		return pesquisaInternacaoFacade;
	}
	
	protected ICadastrosBasicosInternacaoFacade getCadastrosBasicosInternacaoFacade() {
		return this.cadastrosBasicosInternacaoFacade;
	}
	
}
