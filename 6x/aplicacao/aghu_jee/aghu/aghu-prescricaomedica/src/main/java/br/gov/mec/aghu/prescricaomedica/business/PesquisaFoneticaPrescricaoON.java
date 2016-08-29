package br.gov.mec.aghu.prescricaomedica.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioListaOrigensAtendimentos;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.PesquisaFoneticaPrescricaoVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.fonetizador.FonetizadorUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


@Stateless
public class PesquisaFoneticaPrescricaoON extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -487528606043138446L;
	private static final Log LOG = LogFactory.getLog(PesquisaFoneticaPrescricaoON.class);
	
	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;
	
	@Inject
	private AipPacientesDAO aipPacientesDAO;

	public AipPacientesDAO getAipPacientesDAO() {
		return aipPacientesDAO;
	}

	public void setAipPacientesDAO(AipPacientesDAO aipPacientesDAO) {
		this.aipPacientesDAO = aipPacientesDAO;
	}

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	public List<PesquisaFoneticaPrescricaoVO> pesquisarPorFonemas(Integer firstResult,
			Integer maxResults, String nome, String nomeMae,
			boolean respeitarOrdem, Date dataNascimento,
			DominioListaOrigensAtendimentos listaOrigensAtendimentos)
			throws ApplicationBusinessException {
		List<PesquisaFoneticaPrescricaoVO> lista = null;
		List<String> fonemasPaciente = obterFonemasNaOrdem(nome);
		List<AipPacientes> listaPacientes =  this.getAipPacientesDAO().obterPacientesPorFonemas(fonemasPaciente, null, null, null, respeitarOrdem,
				firstResult, maxResults, listaOrigensAtendimentos, true);
		if(listaPacientes!=null && listaPacientes.size()>0){
			lista = new ArrayList<PesquisaFoneticaPrescricaoVO>();
		}
		for(AipPacientes paciente: listaPacientes){
			PesquisaFoneticaPrescricaoVO vo = new PesquisaFoneticaPrescricaoVO();
			vo.setProntuario(paciente.getProntuario());
			vo.setNomePaciente(paciente.getNome());
			Set<AghAtendimentos> atendimentosSet = paciente.getAghAtendimentos();
			Iterator<AghAtendimentos> it = atendimentosSet.iterator();
			while(it.hasNext()){
				AghAtendimentos atendimento = it.next();
				if((atendimento.getOrigem().equals(DominioOrigemAtendimento.U)||atendimento.getOrigem().equals(DominioOrigemAtendimento.I)||atendimento.getOrigem().equals(DominioOrigemAtendimento.N))&&atendimento.getIndPacAtendimento().equals(DominioPacAtendimento.S)){
					vo.setAlaAndar(atendimento.getUnidadeFuncional().getAndarAlaDescricao());
					if(atendimento.getLeito()!=null){
						vo.setLeitoId(atendimento.getLeito().getLeitoID());
					}
					if(atendimento.getQuarto()!=null){
						vo.setDescricaoQuarto(atendimento.getQuarto().getDescricao());
					}
					if(atendimento.getEspecialidade()!=null){
						vo.setSiglaEspecialidade(atendimento.getEspecialidade().getSigla());
					}
					if(atendimento.getServidor()!=null){
						vo.setEquipe(this.pesquisaInternacaoFacade.buscarNomeUsual(atendimento.getServidor().getId().getVinCodigo(), atendimento.getServidor().getId().getMatricula()));
					}
					lista.add(vo);	
				}
			
			}
		}
		return lista;
	}
	
	public String buscarNomeUsual(Short pVinCodigo, Integer pMatricula) {
		if(pVinCodigo != null && pVinCodigo != 0 && pMatricula != null && pMatricula != 0) {
			return this.pesquisaInternacaoFacade.buscarNomeUsual(pVinCodigo, pMatricula);
		}
		return null;
	}
	
	public List<String> obterFonemasNaOrdem(String nome) throws ApplicationBusinessException {
		if (StringUtils.isBlank(nome)) {
			return new ArrayList<String>(0);
		}

		String pFonema = FonetizadorUtil.obterFonema(nome);
		List<String> fonemas = particionarFonemas(pFonema);

		return fonemas;
	}
	
	private List<String> particionarFonemas(String pFonema) {
		if (StringUtils.isBlank(pFonema)) {
			return new ArrayList<String>(0);
		}

		List<String> fonemas = new ArrayList<String>();

		int o = (pFonema.length() / 6);
		for (int i = 1; i <= o; i++) {
			fonemas.add(pFonema.substring((6 * (i - 1)), (6 * (i - 1)) + 6));
		}

		return fonemas;
	}

	public Long pesquisarPorFonemasCount(String nome, String nomeMae,
			Boolean respeitarOrdem, Date dtNascimento,
			DominioListaOrigensAtendimentos listaOrigensAtendimentos)
			throws ApplicationBusinessException {
		List<String> fonemasPaciente = obterFonemasNaOrdem(nome);
		return this.getAipPacientesDAO().obterPacientesPorFonemasCount(fonemasPaciente, null, null, null, respeitarOrdem, listaOrigensAtendimentos, true);
	}

	
}
