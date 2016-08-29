package br.gov.mec.aghu.cups;


/**
 * Contem os caminhos das Fontes PCL das etiquetas.<br>
 * 
 */
@SuppressWarnings("ucd")
public enum FontesPCL {

	// Para incluir novas fontes basta cria-las abaixo.
	// Para isso apenas copie uma das linhas ja existentes e a altere de forma a conter os novos valores.
	// Apenas nao faca isso na linha que termina com ';'.
	// Deve existir apenas uma linha terminando com ponto e virgula, e esta deve ser a ultima linha. 
	FONTE_AIH( "AIH_204_NORMAL_08.pcl" ),
	FONTE_101_NORMAL_11( "PRONTUARIO_101_NORMAL_11.pcl" ),
	FONTE_102_NORMAL_08( "PRONTUARIO_102_NORMAL_08.pcl" ),
	FONTE_PULSEIRA("PULSEIRA_CLIENTE_200_NORMAL_07.pcl"),
	FONTE_FICHA_PRONTUARIO("FICHA_PRONTUARIO_200_NORMAL_09.pcl"),
	FONTE_CARTEIRINHA( "CARTEIRINHA_CLIENTE_200_NORMAL_09.pcl" );

	
	
	// Align atribute...
	private String nomeArquivoFonte;
	
	/**
	 * Construtor que recebe o nome do arquivo fonte.<br>
	 * @param align : String
	 */
	FontesPCL( String nomeArquivoFonte ) {
		this.setNomeArquivoFonte(nomeArquivoFonte);
	}

	private void setNomeArquivoFonte(String nomeArquivoFonte) {
		this.nomeArquivoFonte = nomeArquivoFonte;
	}

	public String getNomeArquivoFonte() {
		return nomeArquivoFonte;
	}


}