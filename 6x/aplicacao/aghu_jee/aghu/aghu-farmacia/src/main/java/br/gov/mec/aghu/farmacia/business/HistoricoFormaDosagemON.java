package br.gov.mec.aghu.farmacia.business;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.farmacia.dao.AfaFormaDosagemJnDAO;
import br.gov.mec.aghu.model.AfaFormaDosagemJn;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;


@Stateless
public class HistoricoFormaDosagemON extends BaseBusiness implements Serializable {

private static final Log LOG = LogFactory.getLog(HistoricoFormaDosagemON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPrescricaoMedicaFacade prescricaoMedicaFacade;

@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

@Inject
private AfaFormaDosagemJnDAO afaFormaDosagemJnDAO;

	private static final long serialVersionUID = 269211699360927133L;

	public Long pesquisarFormaDosagemJnCount(AfaMedicamento medicamento) {
		
		return getAfaFormaDosagemJnDAO().pesquisarFormaDosagemJnCount(medicamento);
	}
	
	public List<AfaFormaDosagemJn> pesquisarFormaDosagemJn(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AfaMedicamento medicamento) {
		

		List<AfaFormaDosagemJn> listaHistoricoFormaDosagem = 
			getAfaFormaDosagemJnDAO().pesquisarFormaDosagemJn(firstResult, maxResult, orderProperty, asc, medicamento);

		
		for(AfaFormaDosagemJn formaDosagemJn:listaHistoricoFormaDosagem){			
			
			if(formaDosagemJn.getSerMatricula() != null && formaDosagemJn.getSerVinCodigo() != null){
				
				RapServidoresId idRapServidor = new RapServidoresId();
				idRapServidor.setMatricula(formaDosagemJn.getSerMatricula());
				idRapServidor.setVinCodigo(formaDosagemJn.getSerVinCodigo());

				RapServidores servidor = getRegistroColaboradorFacade().obterRapServidor(idRapServidor);

				if(servidor != null){
					RapPessoasFisicas pessoa = servidor.getPessoaFisica();
					if(pessoa != null){
						formaDosagemJn.setNomeResponsavel(pessoa.getNome());						
					}	
				}
			}
			
			if(formaDosagemJn.getUmmSeq() != null){
				
				MpmUnidadeMedidaMedica unidadeMedida = getPrescricaoMedicaFacade().obterUnidadesMedidaMedicaPeloId(formaDosagemJn.getUmmSeq()); 

				if(unidadeMedida.getDescricao() != null){
					formaDosagemJn.setUnidadeMedidaMedicasDescricao(unidadeMedida.getDescricao());
				}	
			}

		}		

		return listaHistoricoFormaDosagem;
	}

	private AfaFormaDosagemJnDAO getAfaFormaDosagemJnDAO(){
		return afaFormaDosagemJnDAO;
	}
	
	private IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return prescricaoMedicaFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}
		
}