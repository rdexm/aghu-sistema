package br.gov.mec.aghu.farmacia.business;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.farmacia.dao.AfaMedicamentoEquivalenteJnDAO;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaMedicamentoEquivalenteJn;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class HistoricoMedicamentoEquivalenteON extends BaseBusiness implements Serializable{

private static final Log LOG = LogFactory.getLog(HistoricoMedicamentoEquivalenteON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

@Inject
private AfaMedicamentoEquivalenteJnDAO afaMedicamentoEquivalenteJnDAO;

@EJB
private IFarmaciaFacade farmaciaFacade;

	private static final long serialVersionUID = 3088442842483416897L;

	public Long pesquisarMedicamentoEquivalenteJnCount(AfaMedicamento medicamento) {

		return getAfaMedicamentoEquivalenteJnDAO().pesquisarMedicamentoEquivalenteJnCount(medicamento);
	}

	public List<AfaMedicamentoEquivalenteJn> pesquisarMedicamentoEquivalenteJn(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AfaMedicamento medicamento) {


		List<AfaMedicamentoEquivalenteJn> listaHistoricoMedicamentoEquivalente = 
			getAfaMedicamentoEquivalenteJnDAO().pesquisarMedicamentoEquivalenteJn(firstResult, maxResult, orderProperty, asc, medicamento);


		for(AfaMedicamentoEquivalenteJn medicamentoEquivalenteJn:listaHistoricoMedicamentoEquivalente){			

			// Seta Nome do Responsável 

			if(medicamentoEquivalenteJn.getSerMatricula() != null && medicamentoEquivalenteJn.getSerVinCodigo() != null){

				RapServidoresId idRapServidor = new RapServidoresId();
				idRapServidor.setMatricula(medicamentoEquivalenteJn.getSerMatricula());
				idRapServidor.setVinCodigo(medicamentoEquivalenteJn.getSerVinCodigo());

				RapServidores servidor = getRegistroColaboradorFacade().obterRapServidor(idRapServidor);

				if(servidor != null){
					RapPessoasFisicas pessoa = servidor.getPessoaFisica();
					if(pessoa != null){
						medicamentoEquivalenteJn.setNomeResponsavel(pessoa.getNome());						
					}	
				}
			}

			// Seta Descrição, Concentração e Unidade de Medida 

			AfaMedicamento med = this.getFarmaciaFacade().obterMedicamento(medicamentoEquivalenteJn.getMedMatCodigoEquivalente());

			if(med != null && med.getDescricao() != null){
				
				medicamentoEquivalenteJn.setMedMatCodigoEquivalenteDescricao(med.getDescricao());
			}

			if(med != null && med.getConcentracao() != null){
				
				medicamentoEquivalenteJn.setMedMatCodigoEquivalenteConcentracao(med.getConcentracao());
			}

			if(med != null && med.getMpmUnidadeMedidaMedicas() != null){
				
				MpmUnidadeMedidaMedica unidadeMedida = med.getMpmUnidadeMedidaMedicas(); 

				if(unidadeMedida.getDescricao() != null){
					medicamentoEquivalenteJn.setMedMatCodigoEquivalenteUmmDescricao(unidadeMedida.getDescricao());
				}
			}

		}		

		return listaHistoricoMedicamentoEquivalente;
	}

	private IFarmaciaFacade getFarmaciaFacade() {
		return this.farmaciaFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	private AfaMedicamentoEquivalenteJnDAO getAfaMedicamentoEquivalenteJnDAO(){
		return afaMedicamentoEquivalenteJnDAO;
	}

}
