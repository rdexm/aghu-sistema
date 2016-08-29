package br.gov.mec.aghu.farmacia.business;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.farmacia.dao.AfaSinonimoMedicamentoJnDAO;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaSinonimoMedicamentoJn;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class HistoricoSinonimoMedicamentoON extends BaseBusiness
  implements Serializable
{

private static final Log LOG = LogFactory.getLog(HistoricoSinonimoMedicamentoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AfaSinonimoMedicamentoJnDAO afaSinonimoMedicamentoJnDAO;

@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;
  private static final long serialVersionUID = 7667748398463279153L;

  public Long pesquisarSinonimoMedicamentoJnCount(AfaMedicamento medicamento)
  {
    return getAfaSinonimoMedicamentoJnDAO().pesquisarSinonimoMedicamentoJnCount(medicamento);
  }

  public List<AfaSinonimoMedicamentoJn> pesquisarSinonimoMedicamentoJn(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, AfaMedicamento medicamento)
  {
	  List<AfaSinonimoMedicamentoJn> listaHistoricoSinonimoMedicamento = getAfaSinonimoMedicamentoJnDAO().pesquisarSinonimoMedicamentoJn(firstResult, maxResult, orderProperty, asc, medicamento);

    for (AfaSinonimoMedicamentoJn sinonimoMedicamentoJn : listaHistoricoSinonimoMedicamento)
    {
      if ((sinonimoMedicamentoJn.getSerMatricula() != null) && (sinonimoMedicamentoJn.getSerVinCodigo() != null))
      {
        RapServidoresId idRapServidor = new RapServidoresId();
        idRapServidor.setMatricula(sinonimoMedicamentoJn.getSerMatricula());
        idRapServidor.setVinCodigo(sinonimoMedicamentoJn.getSerVinCodigo());

        RapServidores servidor = getRegistroColaboradorFacade().obterRapServidor(idRapServidor);

        if (servidor != null) {
          RapPessoasFisicas pessoa = servidor.getPessoaFisica();
          if (pessoa != null) {
            sinonimoMedicamentoJn.setNomeResponsavel(pessoa.getNome());
          }
        }
      }

    }

    return listaHistoricoSinonimoMedicamento;
  }

	private AfaSinonimoMedicamentoJnDAO getAfaSinonimoMedicamentoJnDAO() {
		return afaSinonimoMedicamentoJnDAO;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

}